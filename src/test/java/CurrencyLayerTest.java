import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.experimental.theories.suppliers.TestedOn;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.ArrayList.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class CurrencyLayerTest {
    public static final String ROOT = "http://api.currencylayer.com";
    public static final String token = "?access_key=acba22cf72ba22d666cd0e8616ed242c";
    public static final String SUCCESS_FIELD = "success";
    public static final String TERMS_FIELD = "terms";
    public static final String PRIVACY_FIELD = "privacy";
    public static final String TIMESTAMP_FIELD = "timestamp";
    public static final String SOURCE_FIELD = "source";
    public static final String QUOTES_FIELD = "quotes";
    public static final String LIVE_ENDPOINT = "/live";
    public static final String INFO_FIELD = "info";
    public static final String TERMS_URL = "https://currencylayer.com/terms";
    public static final String PRIVACY_URL = "https://currencylayer.com/privacy";
    public static final String USD_CURRENCY_NAME = "USD";
    public static final String CURRENCY_ENDPOINT = "&currencies=";
    public static final String HISTORICAL_ENDPOINT = "/historical";
    public static final String HISTORICAL_FIELD = "historical";
    public static final String DATE_FIELD = "date";


    @Test
    public void unAuthorisedErrorTest() {
        Response response = given().get(ROOT + LIVE_ENDPOINT);
        response.then().statusCode(101);
        response.then().body(SUCCESS_FIELD, equalTo(false));
        response.then().body("error.code", equalTo(101));
        response.then().body("error.type", equalTo("missing_access_key"));
        response.then().body("error.info", equalTo("You have not supplied an API Access Key. [Required format: access_key=YOUR_ACCESS_KEY]"));
    }

    @Test
    public void wrongTokenErrorTest() {
        Response response = given().get(ROOT + LIVE_ENDPOINT + token + "123");
        response.then().statusCode(103);
        response.then().body(INFO_FIELD, hasItem("You have not supplied a valid API Access Key. [Technical Support: support@apilayer.com]"));
    }

    @Test
    public void currencyListResponseTest() {
        Response response = given().contentType("application/json").get(ROOT + LIVE_ENDPOINT + token);
        System.out.println(response.asString());
        response.then().statusCode(200);
    }

    @Test
    public void liveEndpointValuesTest() {
        Response response = given().contentType("application/json").get(ROOT + LIVE_ENDPOINT + token);
        System.out.println(response.asString());
        response.then().body(SUCCESS_FIELD, notNullValue());
        response.then().body(TERMS_FIELD, equalTo(TERMS_URL));
        response.then().body(PRIVACY_FIELD, equalTo(PRIVACY_URL));
        response.then().body(TIMESTAMP_FIELD, notNullValue());
        response.then().body(SOURCE_FIELD, equalTo(USD_CURRENCY_NAME));
        response.then().body(QUOTES_FIELD, notNullValue());
    }

    @Test
    public void wrongEndpointTest() {
        Response response = given().contentType("application/json").get(ROOT + "/liv" + token);
        response.then().statusCode(103);
        response.then().body("error.info", equalTo("This API Function does not exist."));

    }

    @Test
    public void CurrenciesTest() {
        Response response = given().contentType("application/json").get(ROOT + LIVE_ENDPOINT + token + CURRENCY_ENDPOINT+"CAD,RUB,NIS,USD");
        response.then().body(SUCCESS_FIELD, notNullValue());
        response.then().body(TERMS_FIELD, equalTo(TERMS_URL));
        response.then().body(PRIVACY_FIELD, equalTo(PRIVACY_URL));
        response.then().body(TIMESTAMP_FIELD, notNullValue());
        response.then().body(SOURCE_FIELD, equalTo(USD_CURRENCY_NAME));
        response.then().body("quotes.USDCAD", notNullValue());
        response.then().body("quotes.USDRUB", notNullValue());
        response.then().body("quotes.USDNIS", notNullValue());
        response.then().body("quotes.USDUSD", notNullValue());

    }

    @ParameterizedTest
    @ValueSource(strings={"USD","CAD","EUR","NIS"})
    public void MainCurrenciesTest(String currency) {
        Response response = given().contentType("application/json").get(ROOT + LIVE_ENDPOINT + token + CURRENCY_ENDPOINT + currency);
        response.then().statusCode(200);
        response.then().body(SUCCESS_FIELD, notNullValue());
        response.then().body(TERMS_FIELD, equalTo(TERMS_URL));
        response.then().body(PRIVACY_FIELD, equalTo(PRIVACY_URL));
        response.then().body(TIMESTAMP_FIELD, notNullValue());
        response.then().body(SOURCE_FIELD, equalTo(USD_CURRENCY_NAME));
        response.then().body(QUOTES_FIELD, notNullValue());
    }

    @Test
    public void CADCurrenciesTest() {
        Response response = given().contentType("application/json").get(ROOT + LIVE_ENDPOINT + token + CURRENCY_ENDPOINT+"CAD");
        response.then().body("quotes.USDCAD", notNullValue());
    }

    @Test
    public void EURCurrenciesTest() {
        Response response = given().contentType("application/json").get(ROOT + "/live" + token + CURRENCY_ENDPOINT+"EUR");
        response.then().body("quotes.USDEUR", notNullValue());
    }

    @Test
    public void NISCurrenciesTest() {
        Response response = given().contentType("application/json").get(ROOT + LIVE_ENDPOINT + token + CURRENCY_ENDPOINT+"NIS");
        response.then().body("quotes.USDNIS", notNullValue());
    }

    @Test
    public void invalidCurrencyTest() {
        Response response = given().contentType("application/json").get(ROOT + LIVE_ENDPOINT + token + CURRENCY_ENDPOINT+"BBB");
        response.then().statusCode(202);
        response.then().body("error.info", equalTo("You have provided one or more invalid Currency Codes. [Required format: currencies=EUR,USD,GBP,...]"));
    }

    @Test
    public void historicalMissingDateTest() {
        Response response = given().contentType("application/json").get(ROOT + HISTORICAL_ENDPOINT + token + "&Date=");
        response.then().statusCode(301);
        response.then().body("error.info", equalTo("You have not specified a date. [Required format: date=YYYY-MM-DD]"));
    }

    @Test
    public void historicalInvalidDateTest() {
        Response response = given().contentType("application/json").get(ROOT + HISTORICAL_ENDPOINT + token + "&Date=01");
        response.then().statusCode(302);
        response.then().body("error.info", equalTo("You have entered an invalid date. [Required format: date=YYYY-MM-DD]"));
    }

    @Test
    public void historicalDateNoResultsTest() {
        Response response = given().contentType("application/json").get(ROOT + HISTORICAL_ENDPOINT + token + "&Date=1900-01-01");
        response.then().statusCode(106);
        response.then().body("error.info", equalTo("Your query did not return any results. Please try again."));
    }

    @Test
    public void historicalDateTest() {
        Response response = given().contentType("application/json").get(ROOT + HISTORICAL_ENDPOINT + token + "&Date=2020-04-02");
        response.then().statusCode(200);
        response.then().body(HISTORICAL_FIELD, notNullValue());
        response.then().body(TIMESTAMP_FIELD, equalTo(1585871999));
        response.then().body(DATE_FIELD, equalTo("2020-04-02"));
        response.then().body("quotes.USDUSD", notNullValue());
        response.then().body("quotes.USDCAD", notNullValue());
        response.then().body("quotes.USDEUR", notNullValue());
        response.then().body("quotes.USDNIS", notNullValue());
    }

    @Test
    public void historicalUSDCurrencyTest() {
        Response response = given().contentType("application/json").get(ROOT + HISTORICAL_ENDPOINT + token + "&Date=2000-01-01" + CURRENCY_ENDPOINT + "USD");
        response.then().statusCode(200);
        response.then().body(HISTORICAL_FIELD, equalTo(true));
        response.then().body(TIMESTAMP_FIELD, equalTo(946771199));
        response.then().body(DATE_FIELD, equalTo("2000-01-01"));
        response.then().body("quotes.USDUSD", equalTo(1));

    }

    @Test
    public void historicalCADCurrencyTest() {
        Response response = given().contentType("application/json").get(ROOT + HISTORICAL_ENDPOINT + token + "&Date=2019-01-01" + CURRENCY_ENDPOINT+"CAD");
        response.then().statusCode(200);
        response.then().body(HISTORICAL_FIELD, equalTo(true));
        response.then().body(TIMESTAMP_FIELD, equalTo(1546387199));
        response.then().body(DATE_FIELD, equalTo("2019-01-01"));
        response.then().body("quotes.USDCAD", equalTo(1.362635f));

    }

    @Test
    public void historicalRUBCurrencyTest() {
        Response response = given().contentType("application/json").get(ROOT + HISTORICAL_ENDPOINT + token + "&Date=2020-01-01" + CURRENCY_ENDPOINT + "RUB");
        response.then().statusCode(200);
        response.then().body(HISTORICAL_FIELD, equalTo(true));
        response.then().body(TIMESTAMP_FIELD, equalTo(1577923199));
        response.then().body(DATE_FIELD, equalTo("2020-01-01"));
        response.then().body("quotes.USDRUB", equalTo(61.86502f));
    }
}
