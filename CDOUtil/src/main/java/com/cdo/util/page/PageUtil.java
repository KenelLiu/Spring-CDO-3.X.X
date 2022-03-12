package com.cdo.util.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cdo.util.constants.Constants;
import com.cdo.util.http.IPUtil;
import com.cdoframework.cdolib.data.cdo.CDO;

/**
 * 
 * @author KenelLiu
 *
 */
public class PageUtil {		
		private static Log logger=LogFactory.getLog(PageUtil.class);
		
		public static int getPageIndex(CDO cdoRequest) {
			return getPageIndex(cdoRequest, Constants.Page.PAGE_INDEX);
		  }
	
		public static int getPageIndex(CDO cdoRequest, String key){
		    int nPageIndex = 1;
		    try {
		      nPageIndex=cdoRequest.getIntegerValue(key);
		      if(nPageIndex <= 0)
		        nPageIndex = 1;
		    }catch (Exception ex){
	           logger.warn("Can't get PageIndex Field["+key+"] value, Set Default PageIndex Field["+key+"]="+nPageIndex);
		    }
		    return nPageIndex;
		  }

		  public static int getPageSize(CDO cdoRequest) {
		    return getPageSize(cdoRequest, Constants.Page.PAGE_SIZE);
		  }

		  public static int getPageSize(CDO cdoRequest, String key) {
		    int nPageSize = Constants.Page.PAGE_SIZE_MIN;
		    try {
		      nPageSize = cdoRequest.getIntegerValue(key);
		      if ((nPageSize <= 0) || (nPageSize > Constants.Page.PAGE_SIZE_MAX))
		    	  nPageSize = Constants.Page.PAGE_SIZE_MIN;
		    }catch (Exception ex){
		    	 logger.warn("Can't get PageSize Field["+key+"] value, Set Default PageSize Field["+key+"]="+nPageSize);
		    }
		    return nPageSize;
		  }	
		  
			public static void initPage(CDO cdoRequest, CDO cdoResponse){
				int nPageIndex=PageUtil.getPageIndex(cdoRequest);
				int nPageSize=PageUtil.getPageSize(cdoRequest);
				int nStartIndex = (nPageIndex - 1) * nPageSize;
				cdoRequest.setIntegerValue("nStartIndex", nStartIndex);
				cdoRequest.setIntegerValue("nPageIndex", nPageIndex);
				cdoRequest.setIntegerValue("nPageSize", nPageSize);
				cdoRequest.setIntegerValue("nRowNum", (nStartIndex+nPageSize));
				
				cdoResponse.setIntegerValue("nPageIndex", nPageIndex);
				cdoResponse.setIntegerValue("nPageSize", nPageSize);
			}
			
			public static long getPageCount(long nCount,int nPageSize){
				long totalPage = 0;
				if (nCount % nPageSize == 0) {
					totalPage = nCount/nPageSize;
				} else {
					totalPage = nCount/nPageSize + 1;
				}
				return totalPage == 0 ? 1 : totalPage;
			}
			
			public static int getPageCount(int nCount,int nPageSize){
				int totalPage = 0;
				if (nCount % nPageSize == 0) {
					totalPage = nCount/nPageSize;
				} else {
					totalPage = nCount/nPageSize + 1;
				}
				return totalPage == 0 ? 1 : totalPage;
			}
}
