package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.Duration;

public class GPX {

    public static SessionActivity parse(File file) throws Exception {
        String name = file.getName().replace(".gpx", "");
        double totalDistance = 0.0;
        long totalDuration = 0;
        double totalAscent = 0.0;

        double lastLat = Double.NaN;
        double lastLon = Double.NaN;
        double lastEle = Double.NaN;
        
        Instant firstTime = null;
        Instant lastTime = null;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.contains("<trkpt")) {
                    int latIdx = line.indexOf("lat=\"") + 5;
                    int latEnd = line.indexOf("\"", latIdx);
                    int lonIdx = line.indexOf("lon=\"") + 5;
                    int lonEnd = line.indexOf("\"", lonIdx);

                    if (latIdx > 4 && lonIdx > 4) {
                        double lat = Double.parseDouble(line.substring(latIdx, latEnd));
                        double lon = Double.parseDouble(line.substring(lonIdx, lonEnd));

                        if (!Double.isNaN(lastLat)) {
                            totalDistance += calcularDistanciaHaversine(lastLat, lastLon, lat, lon);
                        }
                        lastLat = lat;
                        lastLon = lon;
                    } else {
                        throw new Exception("Estructura de coordenadas inválida");
                    }
                } else if (line.contains("<ele>")) {
                    int inicio = line.indexOf("<ele>") + 5;
                    int fin = line.indexOf("</ele>");
                    if (inicio > 4 && fin > inicio) {
                        double ele = Double.parseDouble(line.substring(inicio, fin));
                        if (!Double.isNaN(lastEle) && ele > lastEle) {
                            totalAscent += (ele - lastEle);
                        }
                        lastEle = ele;
                    }
                } else if (line.contains("<time>")) {
                    int inicio = line.indexOf("<time>") + 6;
                    int fin = line.indexOf("</time>");
                    if (inicio > 5 && fin > inicio) {
                        String timeStr = line.substring(inicio, fin).trim();
                        try {
                            Instant t = Instant.parse(timeStr);
                            if (firstTime == null) {
                                firstTime = t;
                            }
                            lastTime = t;
                        } catch (Exception ex) {
                        }
                    }
                }
            }

            if (firstTime != null && lastTime != null) {
                totalDuration = Duration.between(firstTime, lastTime).getSeconds();
            } else {
                totalDuration = 0;
            }

        } catch (Exception e) {
            throw e;
        }

        return new SessionActivity(name, LocalDateTime.now(), totalDistance, totalDuration, totalAscent);
    }

    private static double calcularDistanciaHaversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
