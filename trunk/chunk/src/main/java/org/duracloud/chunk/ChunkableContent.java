package org.duracloud.chunk;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.duracloud.chunk.manifest.ChunksManifest;
import org.duracloud.chunk.stream.ChunkInputStream;
import org.duracloud.chunk.stream.CountingDigestInputStream;
import org.duracloud.common.error.DuraCloudRuntimeException;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Iterator;

/**
 * This class manages the provided content stream by breaking it chunks of the
 * size specified by maxChunkSize.
 *
 * @author Andrew Woods
 *         Date: Feb 2, 2010
 */
public class ChunkableContent implements Iterable<ChunkInputStream>, Iterator<ChunkInputStream> {

    private final Logger log = Logger.getLogger(getClass());

    private CountingDigestInputStream largeStream;
    private String contentId;
    private long maxChunkSize;
    private long contentSize;

    private ChunkInputStream currentChunk;
    private ChunksManifest manifest;

    private long bytesRead;
    private boolean preserveChunkMD5s = false;

    private static final String DEFAULT_MIME = "application/octet-stream";
    private final int BUFFER_SIZE;

    public ChunkableContent(String contentId,
                            InputStream largeStream,
                            long contentSize,
                            long maxChunkSize) {

        this(contentId, DEFAULT_MIME, largeStream, contentSize, maxChunkSize);
    }

    public ChunkableContent(String contentId,
                            String contentMimetype,
                            InputStream largeStream,
                            long contentSize,
                            long maxChunkSize) {
        BUFFER_SIZE = calculateBufferSize(maxChunkSize);

        this.contentId = contentId;
        this.largeStream = new CountingDigestInputStream(largeStream, true);
        this.maxChunkSize = maxChunkSize;
        this.contentSize = contentSize;
        this.currentChunk = null;
        this.bytesRead = 0;
        this.manifest = new ChunksManifest(this.contentId,
                                           contentMimetype,
                                           contentSize);
    }

    /**
     * This method finds the maximum 1-KB divisor of arg maxChunkSize that is
     * less than 8-KB.
     * It also ensures that arg maxChunkSize is a multiple of 1-KB, otherwise
     * the stream buffering would lose bytes if the maxChunkSize was not
     * divisible by the buffer size.
     * Additionally, by making the buffer multiples of 1-KB ensures efficient
     * block-writing.
     *
     * @param maxChunkSize of chunk stream
     * @return efficient buffer size for given arg chunk-size
     */
    private int calculateBufferSize(long maxChunkSize) {
        final int KB = 1024;

        // Ensure maxChunkSize falls on 1-KB boundaries.
        if (maxChunkSize % KB != 0) {
            String m = "MaxChunkSize must be multiple of 1024: " + maxChunkSize;
            log.error(m);
            throw new DuraCloudRuntimeException(m);
        }

        // Find maximum block factor less than 8-KB.
        long size = maxChunkSize;
        while (size > 8 * KB) {
            size /= 2;
        }

        log.debug("Buf size: " + size + " for maxChunkSize: " + maxChunkSize);
        return (int) size;
    }


    /**
     * This method indicates if there are any more chunks.
     *
     * @return true if more chunks are available.
     */
    public boolean hasNext() {
        return null == currentChunk ||
            (currentChunk.numBytesRead() + bytesRead) < contentSize;
    }

    /**
     * This method returns the next chunk of the wrapped InputStream.
     * <p/>
     * Throws a runtime exception if next() is called before previous stream
     * was fully read.
     *
     * @return next chunk as InputStream
     */
    public ChunkInputStream next() {
        throwIfChunkNotFullyRead();

        // Before return next chunk, catalog current chunk in manifest.
        addEntry();

        long chunkSize = calculateNextChunkSize();
        String chunkId = manifest.nextChunkId();

        InputStream buffIS = new BufferedInputStream(largeStream, BUFFER_SIZE);
        return currentChunk = new ChunkInputStream(chunkId,
                                                   buffIS,
                                                   chunkSize,
                                                   preserveChunkMD5s);
    }

    private void addEntry() {
        if (null != currentChunk) {
            manifest.addEntry(currentChunk.getChunkId(),
                              currentChunk.getMD5(),
                              currentChunk.numBytesRead());

            // Keep running tally of total bytes read.
            bytesRead += currentChunk.numBytesRead();
        } else {
            log.debug("currentChunk is null. No entry added.");
        }
    }

    private long calculateNextChunkSize() {
        long nextSize = contentSize - bytesRead;
        if (nextSize > maxChunkSize) {
            nextSize = maxChunkSize;
        }
        return nextSize;
    }

    private void throwIfChunkNotFullyRead() {
        if (null != currentChunk &&
            currentChunk.numBytesRead() < currentChunk.getChunkSize()) {

            StringBuilder sb = new StringBuilder("Error: ");
            sb.append("Previous chunk not fully read: ");
            sb.append(currentChunk.getChunkId());
            log.error(sb);
            throw new DuraCloudRuntimeException(sb.toString());
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("remove() not supported.");
    }

    public Iterator<ChunkInputStream> iterator() {
        return this;
    }

    public void setPreserveChunkMD5s(boolean preserveChunkMD5s) {
        this.preserveChunkMD5s = preserveChunkMD5s;
    }

    public long getMaxChunkSize() {
        return maxChunkSize;
    }

    public String getChecksum() {
        return largeStream.getMD5();
    }    

    public ChunksManifest finalizeManifest() {
        addEntry();
        
        manifest.setMD5OfSourceContent(largeStream.getMD5());
        IOUtils.closeQuietly(largeStream);
        return manifest;
    }
}