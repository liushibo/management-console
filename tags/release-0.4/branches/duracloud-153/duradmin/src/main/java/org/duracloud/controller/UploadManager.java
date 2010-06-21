package org.duracloud.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UploadManager {

	private Logger log = LoggerFactory.getLogger(UploadManager.class);
	private Map<String,UploadTask> uploadTaskMap;

	public UploadManager(){
		this.uploadTaskMap = Collections.synchronizedMap(new HashMap<String,UploadTask>());
	}
	
	public UploadTask addUploadTask(UploadTask uploadTask) throws UploadTaskExistsException{
		if(this.uploadTaskMap.containsKey(uploadTask.getId())){
			throw new UploadTaskExistsException();
		}
		return this.uploadTaskMap.put(uploadTask.getId(), uploadTask);
	}
	
	public UploadTask get(String id){
		return this.uploadTaskMap.get(id);
	}
	
	public UploadTask remove(String id){
		log.debug("removing {}", id);
		UploadTask task =  this.uploadTaskMap.remove(id);
		if(task == null){
			log.warn("upload task {} does not exist.", id);
		}else{
			log.info("removed {}", task);
		}
		
		return task;
	}
	
	public List<UploadTask> getUploadTaskList(){
		List<UploadTask> list = new LinkedList<UploadTask>();
		list.addAll(this.uploadTaskMap.values());
		Collections.sort(list);
		return list;
	}
	

}