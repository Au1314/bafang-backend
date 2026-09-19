# 公桩随机生成接口文档

## 1. 接口概述

本模块提供了三个接口用于随机生成公桩数据，方便进行测试和模拟场景。所有接口的统一前缀为 `/api/v1/user/`。

## 2. 接口列表

| 接口路径 | 方法 | 功能描述 |
|---------|------|----------|
| `/random-stations/radius` | POST | 在指定经纬度范围内随机生成指定数量的公桩 |
| `/random-stations/area` | POST | 在指定区域内随机生成指定数量的公桩 |
| `/random-stations/single` | POST | 根据指定字段生成一个公桩（未指定字段随机生成） |

## 3. 接口详情

### 3.1 在指定经纬度范围内随机生成公桩

**接口路径**：`/api/v1/user/random-stations/radius`

**请求方法**：POST

**功能描述**：在以指定经纬度为中心，指定半径范围内随机生成指定数量的公桩。

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| longitude | double | 是 | 中心点经度（-180~180） |
| latitude | double | 是 | 中心点纬度（-90~90） |
| radius | double | 是 | 半径（单位：米） |
| count | int | 是 | 生成数量（1~100） |

**请求示例**：

```http
POST /api/v1/user/random-stations/radius?longitude=116.397128&latitude=39.916527&radius=5000&count=3
```

**响应示例**：

```json
{
  "code": 200,
  "msg": "OK",
  "data": [
    {
      "id": null,
      "name": "快充站123A区",
      "locationName": "朝阳区人民广场",
      "longitude": 116.412345,
      "latitude": 39.921234,
      "currentType": 0,
      "price": 1.88,
      "maxChargeVoltage": 600,
      "maxChargeCurrent": 200,
      "maxChargePower": 120000,
      "maxVehicles": 10,
      "currentVehicles": 3,
      "openStatus": 1,
      "openTime": null,
      "closeTime": null,
      "provider": "星星充电",
      "image": "https://example.com/station_image_1.jpg",
      "description": "24小时营业",
      "rating": 4.5,
      "updateTime": "2026-01-12T11:00:00.000+08:00"
    },
    {
      "id": null,
      "name": "充电桩456服务站",
      "locationName": "海淀区科技园区",
      "longitude": 116.387654,
      "latitude": 39.909876,
      "currentType": 1,
      "price": 1.25,
      "maxChargeVoltage": 220,
      "maxChargeCurrent": 32,
      "maxChargePower": 7040,
      "maxVehicles": 5,
      "currentVehicles": 0,
      "openStatus": 1,
      "openTime": null,
      "closeTime": null,
      "provider": "特来电",
      "image": "https://example.com/station_image_2.jpg",
      "description": "支持快充",
      "rating": 4.2,
      "updateTime": "2026-01-12T11:00:00.000+08:00"
    },
    {
      "id": null,
      "name": "充电站789便民点",
      "locationName": "东城区商业中心",
      "longitude": 116.405432,
      "latitude": 39.917654,
      "currentType": 0,
      "price": 2.10,
      "maxChargeVoltage": 800,
      "maxChargeCurrent": 250,
      "maxChargePower": 200000,
      "maxVehicles": 15,
      "currentVehicles": 7,
      "openStatus": 1,
      "openTime": null,
      "closeTime": null,
      "provider": "国家电网",
      "image": "https://example.com/station_image_3.jpg",
      "description": "环境舒适",
      "rating": 4.7,
      "updateTime": "2026-01-12T11:00:00.000+08:00"
    }
  ]
}
```

### 3.2 在指定区域内随机生成公桩

**接口路径**：`/api/v1/user/random-stations/area`

**请求方法**：POST

**功能描述**：在指定的矩形区域内随机生成指定数量的公桩。

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| startLongitude | double | 是 | 起始经度（-180~180） |
| startLatitude | double | 是 | 起始纬度（-90~90） |
| endLongitude | double | 是 | 结束经度（-180~180，需大于起始经度） |
| endLatitude | double | 是 | 结束纬度（-90~90，需大于起始纬度） |
| count | int | 是 | 生成数量（1~100） |

**请求示例**：

```http
POST /api/v1/user/random-stations/area?startLongitude=116.3&startLatitude=39.9&endLongitude=116.4&endLatitude=40.0&count=2
```

**响应示例**：

```json
{
  "code": 200,
  "msg": "OK",
  "data": [
    {
      "id": null,
      "name": "能源站012站点",
      "locationName": "西城区地铁站",
      "longitude": 116.356789,
      "latitude": 39.954321,
      "currentType": 0,
      "price": 1.95,
      "maxChargeVoltage": 750,
      "maxChargeCurrent": 220,
      "maxChargePower": 165000,
      "maxVehicles": 12,
      "currentVehicles": 4,
      "openStatus": 1,
      "openTime": null,
      "closeTime": null,
      "provider": "南方电网",
      "image": "https://example.com/station_image_4.jpg",
      "description": "停车方便",
      "rating": 4.3,
      "updateTime": "2026-01-12T11:00:00.000+08:00"
    },
    {
      "id": null,
      "name": "智能充电站345旗舰站",
      "locationName": "丰台区购物中心",
      "longitude": 116.389876,
      "latitude": 39.987654,
      "currentType": 1,
      "price": 1.30,
      "maxChargeVoltage": 220,
      "maxChargeCurrent": 30,
      "maxChargePower": 6600,
      "maxVehicles": 8,
      "currentVehicles": 2,
      "openStatus": 0,
      "openTime": null,
      "closeTime": null,
      "provider": "比亚迪",
      "image": "https://example.com/station_image_5.jpg",
      "description": "服务周到",
      "rating": 4.0,
      "updateTime": "2026-01-12T11:00:00.000+08:00"
    }
  ]
}
```

### 3.3 根据指定字段生成单个公桩

**接口路径**：`/api/v1/user/random-stations/single`

**请求方法**：POST

**功能描述**：根据指定的字段生成一个公桩，未指定的字段将随机生成。

**请求体参数**：

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| name | string | 否 | 公桩名称 |
| locationName | string | 否 | 地点名称 |
| longitude | double | 否 | 经度（-180~180） |
| latitude | double | 否 | 纬度（-90~90） |
| currentType | int | 否 | 电流类型：0-直流电，1-交流电 |
| price | double | 否 | 充电价格（元/度） |
| maxChargeVoltage | int | 否 | 最大充电电压（伏特） |
| maxChargeCurrent | int | 否 | 最大充电电流（安培） |
| maxChargePower | int | 否 | 最大充电功率（瓦） |
| maxVehicles | int | 否 | 最大车辆数 |
| currentVehicles | int | 否 | 当前车辆数 |
| openStatus | int | 否 | 开放状态：0-关闭，1-开启 |
| provider | string | 否 | 公桩建设者 |
| image | string | 否 | 演示图片URL |
| description | string | 否 | 描述信息 |
| rating | double | 否 | 评分（0~5） |

**请求示例**：

```http
POST /api/v1/user/random-stations/single
Content-Type: application/json

{
  "name": "自定义充电站",
  "locationName": "北京市天安门广场",
  "longitude": 116.397128,
  "latitude": 39.908720,
  "price": 2.00
}
```

**响应示例**：

```json
{
  "code": 200,
  "msg": "OK",
  "data": {
    "id": null,
    "name": "自定义充电站",
    "locationName": "北京市天安门广场",
    "longitude": 116.397128,
    "latitude": 39.908720,
    "currentType": 0,
    "price": 2.00,
    "maxChargeVoltage": 800,
    "maxChargeCurrent": 250,
    "maxChargePower": 200000,
    "maxVehicles": 15,
    "currentVehicles": 5,
    "openStatus": 1,
    "openTime": null,
    "closeTime": null,
    "provider": "特来电",
    "image": "https://example.com/station_image_6.jpg",
    "description": "设备先进",
    "rating": 4.7,
    "updateTime": "2026-01-12T11:00:00.000+08:00"
  }
}
```

## 4. 响应说明

所有接口的响应结构统一为：

| 字段名 | 类型 | 说明 |
|-------|------|------|
| code | int | 状态码：200-成功，其他-失败 |
| msg | string | 响应消息 |
| data | object/array | 响应数据，根据接口不同返回对象或数组 |

### 4.1 状态码说明

| 状态码 | 说明 |
|-------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 500 | 服务器内部错误 |

## 5. 数据字段说明

| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | Integer | 公桩ID（生成的随机数据中为null） |
| name | String | 公桩名称 |
| locationName | String | 地点名称 |
| longitude | Double | 经度 |
| latitude | Double | 纬度 |
| currentType | Integer | 电流类型：0-直流电，1-交流电 |
| price | Double | 充电价格（元/度） |
| maxChargeVoltage | Integer | 最大充电电压（伏特） |
| maxChargeCurrent | Integer | 最大充电电流（安培） |
| maxChargePower | Integer | 最大充电功率（瓦） |
| maxVehicles | Integer | 最大车辆数 |
| currentVehicles | Integer | 当前车辆数 |
| openStatus | Integer | 开放状态：0-关闭，1-开启 |
| openTime | Date | 开启时间 |
| closeTime | Date | 关闭时间 |
| provider | String | 公桩建设者 |
| image | String | 演示图片URL |
| description | String | 描述信息 |
| rating | Double | 评分（0~5） |
| updateTime | Date | 更新时间 |
