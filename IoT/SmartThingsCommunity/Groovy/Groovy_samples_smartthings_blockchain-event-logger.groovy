 /**
 *  Blockchain Event Logger
 *
 *  Copyright 2018 Xooa
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */
 /*
 * Original source via Brian Keifer:
 *  https://raw.githubusercontent.com/bkeifer/smartthings/master/Logstash%20Event%20Logger/LogstashEventLogger.groovy
 *
 * Modifications by: Arisht Jain:
 *  https://github.com/xooa/samples
 *
 * Changes:
 *  Logs to Xooa blockchain platform instead of Logstash
 */
 
definition(
    name: "Blockchain Event Logger",
    namespace: "xooa",
    author: "Arisht Jain",
    description: "Log SmartThings events to Xooa blockchain platform.",
    category: "Convenience",
    iconUrl: "https://xooa.com/blockchain/img/logo1.png",
    iconX2Url: "https://xooa.com/blockchain/img/logo1.png",
    iconX3Url: "https://xooa.com/blockchain/img/logo1.png"
    )

preferences {
    section("Log these presence sensors:") {
        input "presences", "capability.presenceSensor", multiple: true, required: false
    }
    section("Log these switches:") {
        input "switches", "capability.switch", multiple: true, required: false
    }
    section("Log these switch levels:") {
        input "levels", "capability.switchLevel", multiple: true, required: false
    }
    section("Log these motion sensors:") {
        input "motions", "capability.motionSensor", multiple: true, required: false
    }
    section("Log these temperature sensors:") {
        input "temperatures", "capability.temperatureMeasurement", multiple: true, required: false
    }
    section("Log these humidity sensors:") {
        input "humidities", "capability.relativeHumidityMeasurement", multiple: true, required: false
    }
    section("Log these contact sensors:") {
        input "contacts", "capability.contactSensor", multiple: true, required: false
    }
    section("Log these alarms:") {
        input "alarms", "capability.alarm", multiple: true, required: false
    }
    section("Log these indicators:") {
        input "indicators", "capability.indicator", multiple: true, required: false
    }
    section("Log these CO detectors:") {
        input "codetectors", "capability.carbonMonoxideDetector", multiple: true, required: false
    }
    section("Log these smoke detectors:") {
        input "smokedetectors", "capability.smokeDetector", multiple: true, required: false
    }
    section("Log these water detectors:") {
        input "waterdetectors", "capability.waterSensor", multiple: true, required: false
    }
    section("Log these acceleration sensors:") {
        input "accelerations", "capability.accelerationSensor", multiple: true, required: false
    }
    section("Log these power meters:") {
        input "powermeters", "capability.powerMeter", multiple: true, required: false
    }
    section("Log these energy meters:") {
        input "energymeters", "capability.energyMeter", multiple: true, required: false
    }
    section("Log these locks:") {
        input "locks", "capability.lock", multiple: true, required: false
    }
    section() {
        input "apiToken", "text",
            title: "Xooa Participant API token:"
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"

    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"

    unsubscribe()
    initialize()
}

def initialize() {
    doSubscriptions()
}

def doSubscriptions() {
    subscribe(alarms, "alarm", alarmHandler)
    subscribe(codetectors, "carbonMonoxideDetector", coHandler)
    subscribe(contacts, "contact", contactHandler)
    subscribe(indicators, "indicator", indicatorHandler)
    subscribe(modes, "locationMode", modeHandler)
    subscribe(motions, "motion", motionHandler)
    subscribe(presences, "presence", presenceHandler)
    subscribe(relays, "relaySwitch", relayHandler)
    subscribe(smokedetectors, "smokeDetector", smokeHandler)
    subscribe(switches, "switch", switchHandler)
    subscribe(levels, "level", levelHandler)
    subscribe(temperatures, "temperature", temperatureHandler)
    subscribe(waterdetectors, "water", waterHandler)
    subscribe(location, "location", locationHandler)
    subscribe(accelerations, "acceleration", accelerationHandler)
    subscribe(powermeters, "power", powerHandler)
    subscribe(energymeters, "energy", energyHandler)
    subscribe(locks, "lock", lockHandler)
}

def genericHandler(evt) {
    def bearer = settings.apiToken // bearer will be passed in header as authorisation for the request to Xooa blockchain platform
/*
    log.debug("------------------------------")
    log.debug("date: ${evt.date}")
    log.debug("name: ${evt.name}")
    log.debug("displayName: ${evt.displayName}")
    log.debug("device: ${evt.device}")
    log.debug("deviceId: ${evt.deviceId}")
    log.debug("value: ${evt.value}")
    log.debug("isStateChange: ${evt.isStateChange()}")
    log.debug("id: ${evt.id}")
    log.debug("description: ${evt.description}")
    log.debug("descriptionText: ${evt.descriptionText}")
    log.debug("installedSmartAppId: ${evt.installedSmartAppId}")
    log.debug("isoDate: ${evt.isoDate}")
    log.debug("isDigital: ${evt.isDigital()}")
    log.debug("isPhysical: ${evt.isPhysical()}")
    log.debug("location: ${evt.location}")
    log.debug("locationId: ${evt.locationId}")
    log.debug("source: ${evt.source}")
    log.debug("unit: ${evt.unit}")

   */ 
    def date = evt.isoDate.replaceAll("-","")
    date = date.split('T')[0];
    def json = "["
    json += "\"${evt.deviceId}~${evt.isoDate}\","
    json += "{\"docType\":\"Event\","
    json += "\"displayName\":\"${evt.displayName}\","
    json += "\"device\":\"${evt.device}\","
    json += "\"isStateChange\":\"${evt.isStateChange()}\","
    json += "\"id\":\"${evt.id}\","
    json += "\"description\":\"${evt.description}\","
    json += "\"descriptionText\":\"${evt.descriptionText}\","
    json += "\"installedSmartAppId\":\"${evt.installedSmartAppId}\","
    json += "\"isDigital\":\"${evt.isDigital()}\","
    json += "\"isPhysical\":\"${evt.isPhysical()}\","
    json += "\"deviceId\":\"${evt.deviceId}\","
    json += "\"location\":\"${evt.location}\","
    json += "\"locationId\":\"${evt.locationId}\","
    json += "\"source\":\"${evt.source}\","
    json += "\"unit\":\"${evt.unit}\","
    json += "\"value\":\"${evt.value}\","
    json += "\"name\":\"${evt.name}\","
    json += "\"date\":\"${date}\","
    json += "\"time\":\"${evt.isoDate}\""
    json += "}]"

    // saveNewEvent() function present in smart contract is called in this request. 
    // Modify the endpoint of this URL accordingly if function name is changed
    // Modify the json parameter sent in this request if definition of the function is changed in the smart contract
    def params = [
        uri: "https://api.xooa.com/api/v1/xldb/createupdate",
        headers: [
            "Authorization": "Bearer ${bearer}",
            "content-type": "application/json"
        ],
        body: json
    ]
    log.debug("Params: ${params}")
    try {
        httpPostJson(params) { resp ->
        	log.debug "response from xooa: ${resp.data}, status: ${resp.status}"
            if(resp.status == 202) {
            	def sleepTime = 3000
                def requestCount = 5
                def i = 0
                def responseStatus = 202
            	while (i < requestCount && responseStatus == 202) {
                	pause(sleepTime)
                    def params1 = [
                        uri: "https://api.xooa.com/api/v1/results/${resp.data.resultId}",
                        headers: [
                            "Authorization": "Bearer ${bearer}",
                            "content-type": "application/json"
                        ]
                    ]
                    log.debug("results API params: ${params1}")
                    try {
                		def continueRequest = 0
                    	log.debug "Making API request to check for response."
                        httpGet(params1) { resp1 ->
                            log.debug "response from results API endpoint: ${resp1.data}"
                            if(resp1.status == 200) {
                            	responseStatus = 200
                            } else if (resp1.status == 202) {
                                log.debug "request not processed yet."
                                i++
                                continueRequest = 1
                            }
                        }
                        if(continueRequest == 1){
                        	continue
                        }
                    } catch (groovyx.net.http.HttpResponseException ex) {
                        log.debug "Unexpected response error: ${ex.statusCode}"
                        log.debug ex
                        log.debug ex.response.contentType
                        break
                    }
              	}
            }
        }
    } catch (groovyx.net.http.HttpResponseException ex) {
        if (ex.statusCode < 200 || ex.statusCode >= 300) {
            log.debug "Unexpected response error: ${ex.statusCode}"
            log.debug ex
            log.debug ex.response.contentType
        }
    }
    
    
    
    
    def json1 = "["
    json1 += "\"${evt.deviceId}\","
    json1 += "{\"docType\":\"EventLess\","
    json1 += "\"displayName\":\"${evt.displayName}\","
    json1 += "\"locationId\":\"${evt.locationId}\","
    json1 += "\"value\":\"${evt.value}\","
    json1 += "\"time\":\"${evt.isoDate}\""
    json1 += "}]"

    // Modify the endpoint of this URL accordingly if function name is changed
    // Modify the json parameter sent in this request if definition of the function is changed in the smart contract
    def params2 = [
        uri: "https://api.xooa.com/api/v1/xldb/createupdate",
        headers: [
            "Authorization": "Bearer ${bearer}",
            "content-type": "application/json"
        ],
        body: json1
    ]
    log.debug("Params2: ${params2}")
    try {
        httpPostJson(params2) { resp ->
        	log.debug "response from xooa: ${resp.data}, status: ${resp.status}"
            if(resp.status == 202) {
            	def sleepTime = 3000
                def requestCount = 5
                def i = 0
                def responseStatus = 202
            	while (i < requestCount && responseStatus == 202) {
                	pause(sleepTime)
                    def params3 = [
                        uri: "https://api.xooa.com/api/v1/results/${resp.data.resultId}",
                        headers: [
                            "Authorization": "Bearer ${bearer}",
                            "content-type": "application/json"
                        ]
                    ]
                    log.debug("results API params: ${params1}")
                    try {
                		def continueRequest = 0
                    	log.debug "Making API request to check for response."
                        httpGet(params1) { resp1 ->
                            log.debug "response from results API endpoint: ${resp1.data}"
                            if(resp1.status == 200) {
                            	responseStatus = 200
                            } else if (resp1.status == 202) {
                                log.debug "request not processed yet."
                                i++
                                continueRequest = 1
                            }
                        }
                        if(continueRequest == 1){
                        	continue
                        }
                    } catch (groovyx.net.http.HttpResponseException ex) {
                        log.debug "Unexpected response error: ${ex.statusCode}"
                        log.debug ex
                        log.debug ex.response.contentType
                        break
                    }
              	}
            }
        }
    } catch (groovyx.net.http.HttpResponseException ex) {
        if (ex.statusCode < 200 || ex.statusCode >= 300) {
            log.debug "Unexpected response error: ${ex.statusCode}"
            log.debug ex
            log.debug ex.response.contentType
        }
    }
}

def alarmHandler(evt) {
    genericHandler(evt)
}

def coHandler(evt) {
    genericHandler(evt)
}

def indicatorHandler(evt) {
    genericHandler(evt)
}

def presenceHandler(evt) {
    genericHandler(evt)
}

def switchHandler(evt) {
    genericHandler(evt)
}

def smokeHandler(evt) {
    genericHandler(evt)
}

def levelHandler(evt) {
    genericHandler(evt)
}

def contactHandler(evt) {
    genericHandler(evt)
}

def temperatureHandler(evt) {
    genericHandler(evt)
}

def motionHandler(evt) {
    genericHandler(evt)
}

def modeHandler(evt) {
    genericHandler(evt)
}

def relayHandler(evt) {
    genericHandler(evt)
}

def waterHandler(evt) {
    genericHandler(evt)
}

def locationHandler(evt) {
    genericHandler(evt)
}

def accelerationHandler(evt) {
    genericHandler(evt)
}

def powerHandler(evt) {
    genericHandler(evt)
}

def energyHandler(evt) {
    genericHandler(evt)
}

def lockHandler(evt) {
	genericHandler(evt)
}