## Get best windsuring conditions for a location

External API to get forecast from: Weatherbit API https://www.weatherbit.io/api/weather-forecast-16-day

The best location for windsurfing is calculated with the formula:

*If the wind speed is not within <5; 18> (m/s) and the temperature is not in the range <5; 35> (°C), the location is not suitable for windsurfing. 
However, if they are in these ranges, then the best location is determined by the highest value calculated from the following formula:
v * 3 + temp.*

If none of the locations meets the above criteria, the application does not return any

Sample response:
{
  "city": "Fortalza",
  "country": "Brazil",
  "forecast": {
    "wind-speed": 7.5,
    "temperature": 26.1
  }
}

## Locations
Works for predefined locations:
Jastarnia (Poland), Bridgetown (Barbados), Fortaleza (Brazil), Pissouri (Cyprus), Le Morne (Mauritius)

## How to run
1) Build with maven: ``` mvn package```
2) Run with java: ```java -jar <jar.file>```
3) Go to http://localhost:8080/api/weather/windsurfing-location?date=[providedDate]
providedDate - date in format of yyyy-MM-dd
For example: http://localhost:8080/api/weather/windsurfing-location?date=2022-09-19

## Extend with new locations
Simply add new location to ```locations.json``` file
