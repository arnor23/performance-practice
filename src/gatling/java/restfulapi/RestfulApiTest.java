package restfulapi;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.core.CoreDsl.ElFileBody;
import static io.gatling.javaapi.http.HttpDsl.*;

public class RestfulApiTest extends Simulation {

    String baseUrl = System.getProperty("baseUrl", "https://api.restful-api.dev/objects");
    String concurrentUsers = System.getProperty("concurrentUsers", "5");

    FeederBuilder.FileBased<Object> feeder = jsonFile("data/appliances.json").circular();

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl(baseUrl);

    // Scenario
    ScenarioBuilder scn = scenario("Appliances API Test")
            .feed(feeder)

            .exec(http("Post Appliance")
                    .post("/#name")
                    .check(status().is(200)))

            .exec(http("Update Appliance")
                    .patch("/#id")
                    .body(ElFileBody("data/PutBody.json"))
                    .check(status().is(200)))


            .exec(http("Get Appliance")
                    .get("/#{id}")
                    .check(jmesPath("name").isEL("#{name}"))
                    .check(status().is(200)))
            ;

    {
        setUp(
                scn.injectClosed(
                        constantConcurrentUsers(Integer.parseInt(concurrentUsers)).during(Duration.ofSeconds(10)
                        )
                )
        ).protocols(httpProtocol);
    }





}
