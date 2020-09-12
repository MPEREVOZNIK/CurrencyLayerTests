import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.experimental.theories.suppliers.TestedOn;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.ArrayList.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class CurrencyLayerTest {
    final static String ROOT = "http://api.currencylayer.com";
    final static String token = "?access_key=acba22cf72ba22d666cd0e8616ed242c";

    @Test
    public void unAuthorisedErrorTest() {
        Response response = given().get(ROOT + "/live");
        response.then().statusCode(101);
        response.then().body("success",equalTo(false));
        response.then().body("error.code", equalTo(101));
        response.then().body("error.type", equalTo("missing_access_key"));
        response.then().body("error.info", equalTo("You have not supplied an API Access Key. [Required format: access_key=YOUR_ACCESS_KEY]"));
    }

    @Test
    public void wrongTokenErrorTest() {
        Response response = given().get(ROOT + "/live" + token + "123");
        response.then().statusCode(103);
        response.then().body("info", hasItem("You have not supplied a valid API Access Key. [Technical Support: support@apilayer.com]"));
    }

    @Test
    public void currencyListResponseTest() {
        Response response = given().contentType("application/json").get(ROOT + "/live" + token);
        System.out.println(response.asString());
        response.then().statusCode(200);
    }

    @Test
    public void liveEndpointValuesTest() {
        Response response = given().contentType("application/json").get(ROOT + "/live" + token);
        System.out.println(response.asString());
        response.then().body("success", notNullValue());
        response.then().body("terms", equalTo("https://currencylayer.com/terms"));
        response.then().body("privacy", equalTo("https://currencylayer.com/privacy"));
        response.then().body("timestamp", notNullValue());
        response.then().body("source", equalTo("USD"));
        response.then().body("quotes", notNullValue());
    }

    @Test
    public void wrongEndpointTest() {
        Response response = given().contentType("application/json").get(ROOT + "/liv" + token);
        response.then().statusCode(103);
        response.then().body("error.info", equalTo("This API Function does not exist."));

    }

    @Test
    public void mainCurrenciesTest() {
        Response response = given().contentType("application/json").get(ROOT + "/live" + token + "&currencies=CAD,RUB,NIS,USD");
        response.then().body("success", notNullValue());
        response.then().body("terms", equalTo("https://currencylayer.com/terms"));
        response.then().body("privacy", equalTo("https://currencylayer.com/privacy"));
        response.then().body("timestamp", notNullValue());
        response.then().body("source", equalTo("USD"));
        response.then().body("quotes.USDCAD", notNullValue());
        response.then().body("quotes.USDRUB", notNullValue());
        response.then().body("quotes.USDNIS", notNullValue());
        response.then().body("quotes.USDUSD", notNullValue());

    }

    @Test
    public void USDCurrenciesTest() {
        Response response = given().contentType("application/json").get(ROOT + "/live" + token + "&currencies=USD");
        response.then().statusCode(200);
        response.then().body("success", notNullValue());
        response.then().body("terms", equalTo("https://currencylayer.com/terms"));
        response.then().body("privacy", equalTo("https://currencylayer.com/privacy"));
        response.then().body("timestamp", notNullValue());
        response.then().body("source", equalTo("USD"));
        response.then().body("quotes.USDUSD", notNullValue());
    }

    @Test
    public void CADCurrenciesTest() {
        Response response = given().contentType("application/json").get(ROOT + "/live" + token + "&currencies=CAD");
        response.then().statusCode(200);
        response.then().body("success", notNullValue());
        response.then().body("terms", equalTo("https://currencylayer.com/terms"));
        response.then().body("privacy", equalTo("https://currencylayer.com/privacy"));
        response.then().body("timestamp", notNullValue());
        response.then().body("source", equalTo("USD"));
        response.then().body("quotes.USDCAD", notNullValue());
    }

    @Test
    public void EURCurrenciesTest() {
        Response response = given().contentType("application/json").get(ROOT + "/live" + token + "&currencies=EUR");
        response.then().statusCode(200);
        response.then().body("success", notNullValue());
        response.then().body("terms", equalTo("https://currencylayer.com/terms"));
        response.then().body("privacy", equalTo("https://currencylayer.com/privacy"));
        response.then().body("timestamp", notNullValue());
        response.then().body("source", equalTo("USD"));
        response.then().body("quotes.USDEUR", notNullValue());
    }

    @Test
    public void NISCurrenciesTest() {
        Response response = given().contentType("application/json").get(ROOT + "/live" + token + "&currencies=NIS");
        response.then().statusCode(200);
        response.then().body("success", notNullValue());
        response.then().body("terms", equalTo("https://currencylayer.com/terms"));
        response.then().body("privacy", equalTo("https://currencylayer.com/privacy"));
        response.then().body("timestamp", notNullValue());
        response.then().body("source", equalTo("USD"));
        response.then().body("quotes.USDNIS", notNullValue());
    }

    @Test
    public void invalidCurrencyTest() {
        Response response = given().contentType("application/json").get(ROOT + "/live" + token + "&currencies=BBB");
        response.then().statusCode(202);
        response.then().body("error.info", equalTo("You have provided one or more invalid Currency Codes. [Required format: currencies=EUR,USD,GBP,...]"));
    }

    @Test
    public void historicalMissingDateTest() {
        Response response = given().contentType("application/json").get(ROOT + "/historical" + token + "&Date=");
        response.then().statusCode(301);
        response.then().body("error.info", equalTo("You have not specified a date. [Required format: date=YYYY-MM-DD]"));
    }

    @Test
    public void historicalInvalidDateTest() {
        Response response = given().contentType("application/json").get(ROOT + "/historical" + token + "&Date=01");
        response.then().statusCode(302);
        response.then().body("error.info", equalTo("You have entered an invalid date. [Required format: date=YYYY-MM-DD]"));
    }

    @Test
    public void historicalDateNoResultsTest() {
        Response response = given().contentType("application/json").get(ROOT + "/historical" + token + "&Date=1900-01-01");
        response.then().statusCode(106);
        response.then().body("error.info", equalTo("Your query did not return any results. Please try again."));
    }

    @Test
    public void historicalDateTest() {
        Response response = given().contentType("application/json").get(ROOT + "/historical" + token + "&Date=2020-04-02");
        response.then().statusCode(200);
        response.then().body("historical", notNullValue());
        response.then().body("timestamp", equalTo(1585871999));
        response.then().body("date", equalTo("2020-04-02"));
        response.then().body("quotes.USDUSD", notNullValue());
        response.then().body("quotes.USDCAD", notNullValue());
        response.then().body("quotes.USDEUR", notNullValue());
        response.then().body("quotes.USDNIS", notNullValue());
    }

    @Test
    public void historicalUSDCurrencyTest() {
        Response response = given().contentType("application/json").get(ROOT + "/historical" + token + "&Date=2000-01-01" + "&currencies=USD");
        response.then().statusCode(200);
        response.then().body("historical", equalTo(true));
        response.then().body("timestamp", equalTo(946771199));
        response.then().body("date", equalTo("2000-01-01"));
        response.then().body("quotes.USDUSD", equalTo(1));

    }

    @Test
    public void historicalCADCurrencyTest() {
        Response response = given().contentType("application/json").get(ROOT + "/historical" + token + "&Date=2019-01-01" + "&currencies=CAD");
        response.then().statusCode(200);
        response.then().body("historical", equalTo(true));
        response.then().body("timestamp", equalTo(1546387199));
        response.then().body("date", equalTo("2019-01-01"));
        response.then().body("quotes.USDCAD", equalTo(1.362635f));

    }

    @Test
    public void historicalRUBCurrencyTest() {
        Response response = given().contentType("application/json").get(ROOT + "/historical" + token + "&Date=2020-01-01" + "&currencies=RUB");
        response.then().statusCode(200);
        response.then().body("historical", equalTo(true));
        response.then().body("timestamp", equalTo(1577923199));
        response.then().body("date", equalTo("2020-01-01"));
        response.then().body("quotes.USDRUB", equalTo(61.86502f));
    }
}
