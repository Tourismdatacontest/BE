package com.tourismdata.contest.global.util;

// MySQL은 PostGIS의 ST_DWithin 같은 공간 함수가 없어, 애플리케이션 레벨(Haversine 공식)로
// 두 GPS 좌표 간 거리를 계산한다. 프로젝트 규모(남한산성 7.6km, 체크포인트 한정 개수)에서는
// 공간 인덱스 없이도 이 방식으로 충분한 성능이 나온다.
public class GeoUtils {

    private static final double EARTH_RADIUS_METERS = 6_371_000;

    private GeoUtils() {
    }

    /**
     * 두 GPS 좌표 사이의 거리를 미터 단위로 계산한다 (Haversine 공식).
     */
    public static double distanceInMeters(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }

    /**
     * 현재 위치가 대상 좌표의 radiusMeters 반경 이내인지 판정한다.
     * 제안서 기준 체크포인트 트리거 반경은 150m.
     */
    public static boolean isWithinRadius(double currentLat, double currentLon,
                                          double targetLat, double targetLon,
                                          double radiusMeters) {
        return distanceInMeters(currentLat, currentLon, targetLat, targetLon) <= radiusMeters;
    }
}
