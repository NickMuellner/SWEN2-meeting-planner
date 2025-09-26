package org.example.meetingplanner.api;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenRouteServicesAPITest {

    @Test
    void searchSingleLocation() throws ExecutionException, InterruptedException {
        OpenRouteServicesAPI openRouteServicesAPI = new OpenRouteServicesAPI();
        GeoCode geoCode = openRouteServicesAPI.search("Vienna").get();

        assertTrue(geoCode.getLat() >= -90 && geoCode.getLat() <= 90);
        assertTrue(geoCode.getLon() >= -180 && geoCode.getLon() <= 180);
        assertEquals(48, geoCode.getLat().intValue());
        assertEquals(16, geoCode.getLon().intValue());
    }

    @Test
    void searchRoute() throws ExecutionException, InterruptedException {
        OpenRouteServicesAPI openRouteServicesAPI = new OpenRouteServicesAPI();
        List<GeoCode> geoCodes = openRouteServicesAPI.searchRoute("Vienna", "Berlin").get();

        for (GeoCode geoCode : geoCodes) {
            assertTrue(geoCode.getLat() >= -90 && geoCode.getLat() <= 90);
            assertTrue(geoCode.getLon() >= -180 && geoCode.getLon() <= 180);
        }
    }
}