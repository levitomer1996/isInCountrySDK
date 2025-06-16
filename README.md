
# 🌍 IsInCountry SDK

A lightweight SDK and API service that determines whether a geographic coordinate is inside a specific country using **GeoJSON polygons**.

---

## ✨ Features

- ✅ Check if a coordinate is within a country’s borders
- 📦 Store and manage GeoJSON polygon and multipolygon data
- 🌐 RESTful API built with NestJS and MongoDB
- 🖥️ Admin Portal with:
  - Country polygon manager
  - Map-based testing interface
  - Activity log viewer

---

## 📱 Android SDK Usage

Use **Retrofit** or any HTTP client to interact with the service. Send coordinates and country code — receive a response indicating if it's inside.

### Example Request
```json
POST /countries/check-location
{
  "lat": 32.1,
  "lng": 34.8,
  "code": "IL"
}
```

### Example Response
```json
{
  "inside": true
}
```

---

## 🏗️ API Reference

### 🔍 Check If Location is in Country
**POST** `/countries/check-location`

#### Request Body:
```json
{
  "lat": 32.1,
  "lng": 34.8,
  "code": "IL"
}
```

#### Response:
```json
{
  "inside": true
}
```

---

### ➕ Create a Country
**POST** `/countries/create`

Add a country using a name, ISO code, and valid GeoJSON polygon or multipolygon.

#### Example:
```json
{
  "name": "Israel",
  "code": "IL",
  "geoJson": {
    "type": "Polygon",
    "coordinates": [
      [
        [35.0, 32.0],
        [35.1, 32.0],
        [35.1, 32.1],
        [35.0, 32.1],
        [35.0, 32.0]
      ]
    ]
  }
}
```

✅ Ensure:
- Coordinates are in `[longitude, latitude]` order
- The first and last points **close the ring**

---

### 📄 Get All Countries
**GET** `/countries`

Returns a list of all stored countries with their polygons.

---

## ⚙️ Internal Architecture

- **Backend**: NestJS (Node.js) with MongoDB via Mongoose
- **Polygon Checks**: Uses `@turf/boolean-point-in-polygon` to determine spatial inclusion
- **Storage**: Countries stored with unique ISO code and GeoJSON polygons

---

## 🛠️ Admin Portal Features

- 📍 **Country Management**: View/add/edit GeoJSON data
- 🔎 **Test Tool**: Check if a coordinate lies inside any country
- 📜 **Logs**: View recent API request logs

---

## 📦 Technologies Used

| Layer        | Stack                     |
|--------------|---------------------------|
| Backend API  | NestJS + MongoDB          |
| Spatial Calc | Turf.js                   |
| SDK          | Android (Kotlin)          |
| Portal       | ReactJS + Maplibre        |

---

## 🚀 Getting Started

Clone this repository and follow the instructions for:

- 🔧 [Backend Setup](#)
- 📱 [Android SDK Integration](#)
- 🖥️ [Admin Portal Usage](#)

> You can also pull the Android SDK via [JitPack](https://jitpack.io/#levitomer1996/isInCountrySDK)

```groovy
implementation 'com.github.levitomer1996:isInCountrySDK:v1.0.0'
```

---

## 📫 Contributing

Have polygons or want to extend the country database?  
Submit a PR or contact us to contribute your data!

---

## 📄 License

This project is licensed under the MIT License.
