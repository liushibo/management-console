
package org.duracloud.duradmin.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.duracloud.duradmin.contentstore.ContentItemList;
import org.duracloud.duradmin.contentstore.ContentStoreProvider;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.ControllerUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class ContentsController
        extends BaseCommandController {

    protected final Logger log = Logger.getLogger(getClass());

    private String successView;

    public ContentsController() {
        setCommandClass(Space.class);
        setCommandName("space");
    }

    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Object command,
                                  BindException errors) throws Exception {
        Space space = (Space) command;
        String spaceId = space.getSpaceId();
        ControllerUtils.checkSpaceId(spaceId);
        ContentItemList contentItemList = getContentItemList(request, spaceId);

        String maxPerPage = request.getParameter("mpp");
        if (StringUtils.hasText(maxPerPage)) {
            contentItemList.setMaxResultsPerPage(Integer.parseInt(maxPerPage));
        }

        String action = space.getAction();
        if (StringUtils.hasText(action)) {
            if (action.equals("n")) {
                contentItemList.next();
            } else if (action.equals("p")) {
                contentItemList.previous();
            } else {
                contentItemList.first();
            }
        }

        ModelAndView mav = new ModelAndView();
        mav.addObject("contentItemList", contentItemList);
        mav.addObject("space", contentItemList.getSpace());
        mav.setViewName(getSuccessView());
        return mav;
    }

    public String getSuccessView() {
        return successView;
    }

    private ContentItemList getContentItemList(HttpServletRequest request,
                                               String spaceId) {
        HttpSession session = request.getSession();
        ContentItemList list =
                (ContentItemList) session.getAttribute("content-list-"
                        + spaceId);
        if (list == null) {
            list = new ContentItemList(spaceId, getContentStoreProvider());
            session.setAttribute("content-list-" + spaceId, list);
        }
        return list;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

}