package com.meizu.lichee.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SignUtils {

	private static final Logger logger = LoggerFactory.getLogger( SignUtils.class );

	public static String signUrl( Map<String, Object> parameter, String signKey ) {

		if( null == parameter || parameter.isEmpty() ) {
			return "";
		}

		List<String> sortCache = new ArrayList<String>();
		for( Map.Entry<String, Object> en : parameter.entrySet() ) {
			sortCache.add( en.getKey() + "=" + en.getValue() );
		}

		Collections.sort( sortCache );

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for( String p : sortCache ) {
			if( first ) {
				first = false;
			} else {
				sb.append( '&' );
			}
			sb.append( p );
		}

		sb.append( ':' ).append( signKey );
		logger.debug("signContent: {}", sb.toString());
		return MD5Utils.digest( sb.toString(), "UTF-8" );
	}
}