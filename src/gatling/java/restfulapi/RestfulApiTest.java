package restfulapi;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class RestfulApiTest extends Simulation {

    String baseUrl = System.getProperty("baseUrl", "https://api.restful-api.dev/objects");

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
                    .check(status().is(200)))

            .exec(http("Get Appliance")
                    .get("/#{id}")
                    .check(jmesPath("name").isEL("#{name}"))
                    .check(status().is(200)))
            ;

    {
        setUp(
                scn.injectOpen(
                        rampUsers(10).during(10)
                )
        ).protocols(httpProtocol);
    }





}
