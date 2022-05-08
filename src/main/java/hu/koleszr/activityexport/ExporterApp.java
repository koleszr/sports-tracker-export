package hu.koleszr.activityexport;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class ExporterApp {

    private static final String ACTIVITIES_URL = "https://api.sports-tracker.com/apiserver/v1/workouts?limited=true&limit=1000000";
    private static final String ACTIVITY_DOWNLOAD_URL = "https://api.sports-tracker.com/apiserver/v1/workout/exportGpx";
    private static final String AUTHORIZATION_HEADER = "STTAuthorization";

    private static final ObjectMapper mapper = new ObjectMapper().configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
            false
    );

    private static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 1) {
            System.err.printf("Usage:%n<download directory>");
            System.exit(1);
        }

        final var downloadPath = Path.of(args[0]);
        if (!downloadPath.isAbsolute()) {
            throw new IllegalArgumentException("Download path must be absolute, e.g. C:\\Users\\user\\Downloads");
        }

        final var authorizationToken = readAuthorizationToken();

        System.out.println("Getting activities!");
        final var downloadCount = getActivities(authorizationToken)
                .stream()
                .map(activityId -> downloadActivity(downloadPath, activityId, authorizationToken))
                .reduce(0, (acc, success) -> success ? acc + 1 : acc, (a, __) -> a);

        System.out.printf("Downloaded %d activities to %s%n", downloadCount, downloadPath);
    }

    private static Activities getActivities(String authorizationToken) throws IOException, InterruptedException {
        final var activitiesRequest = HttpRequest
                .newBuilder(URI.create(ACTIVITIES_URL))
                .GET()
                .header(AUTHORIZATION_HEADER, authorizationToken)
                .build();

        final var response = client.send(activitiesRequest, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), Activities.class);
    }

    private static String readAuthorizationToken() {
        System.out.print("Enter your authorization token: ");
        return new Scanner(System.in).nextLine();
    }

    private static boolean downloadActivity(
            Path downloadPath,
            Activity activity,
            String authorizationToken
    ) {
        final var downloadUrl = String.format(
                "%s/%s?token=%s",
                ACTIVITY_DOWNLOAD_URL,
                activity.workoutKey(),
                authorizationToken
        );
        final var downloadRequest = HttpRequest
                .newBuilder(URI.create(downloadUrl))
                .GET()
                .build();

        try {
            client.send(
                    downloadRequest,
                    HttpResponse.BodyHandlers.ofFileDownload(
                            downloadPath,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.WRITE,
                            StandardOpenOption.TRUNCATE_EXISTING
                    )
            );
            System.out.printf("Successfully downloaded activity from %s!%n", activity.localStartTime());
            return true;
        } catch (IOException | InterruptedException e) {
            System.err.printf("Failed to get activity from %s due %s!%n", activity.localStartTime(), e.getMessage());
            return false;
        }
    }
}
