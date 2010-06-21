/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradmin.control;

import org.duracloud.client.ContentStore;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.duradmin.util.ControllerUtils;
import org.duracloud.duradmin.util.SpaceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ContentItemDataController
        extends BaseCommandController {

    protected final Logger log = LoggerFactory.getLogger(ContentItemDataController.class);

    public ContentItemDataController() {
        setCommandClass(ContentItem.class);
        setCommandName("contentItem");
    }

    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Object command,
                                  BindException errors) throws Exception {
        ContentItem contentItem = (ContentItem) command;
        String spaceId = contentItem.getSpaceId();
        String contentId = contentItem.getContentId();
        ControllerUtils.checkContentItemId(spaceId, contentId);
        ContentStore store = getContentStore();
        SpaceUtil.populateContentItem(contentItem,
                                      spaceId,
                                      contentId,
                                      store,
                                      getServicesManager());

        ModelAndView mav = new ModelAndView();
        mav.setViewName("jsonView");
        mav.getModel().clear();
        mav.addObject(contentItem);
        return mav;
    }

}