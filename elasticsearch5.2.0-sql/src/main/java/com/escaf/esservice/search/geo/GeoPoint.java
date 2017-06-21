package com.escaf.esservice.search.geo;

/**
 * geo-location used for
 * #{@link org.springframework.data.elasticsearch.core.query.Criteria}.
 *
 * @author Franck Marchand
 * @author Mohsin Husen
 */
public class GeoPoint {

	private double lat;
	private double lon;

	@SuppressWarnings("unused")
	private GeoPoint() {

	}

	public GeoPoint(double latitude, double longitude) {
		this.lat = latitude;
		this.lon = longitude;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	/**
	 * build a GeoPoint from a {@link org.springframework.data.geo.Point}
	 *
	 * @param point
	 *            {@link org.springframework.data.geo.Point}
	 * @return a
	 *         {@link com.escaf.esservice.core.support.geo.elasticsearch.core.geo.GeoPoint}
	 */
	public static GeoPoint fromPoint(Point point) {
		return new GeoPoint(point.getX(), point.getY());
	}

	public static Point toPoint(GeoPoint point) {
		return new Point(point.getLat(), point.getLon());
	}
}
