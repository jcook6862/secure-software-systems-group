/**
 *  Child Ultrasonic Sensor
 *
 *  Copyright 2017 Daniel Ogorchock
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Change History:
 *
 *    Date        Who            What
 *    ----        ---            ----
 *    2018-06-02  Dan Ogorchock  Revised/Simplified for Hubitat Composite Driver Model
 *    2019-05-05  CSC		 Display the remaining water in tank in % and remaining water in liters
 *
 * 
 */
metadata {
	definition (name: "Child Ultrasonic Sensor", namespace: "ogiewon", author: "Daniel Ogorchock") {
		capability "Sensor"
        
		attribute "lastUpdated", "String"
        attribute "ultrasonic", "Number"
    }

	tiles(scale: 2) {
		multiAttributeTile(name: "ultrasonic", type: "generic", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.ultrasonic", key: "PRIMARY_CONTROL") {
				attributeState("ultrasonic", label: '${currentValue}%', unit:"%", defaultState: true, 
						backgroundColors: [
							[value: 80, color: "#00FF21"],
							[value: 50, color: "#FFA700"],
							[value: 20, color: "#FF0000"]
						])
			}
            tileAttribute ("device.liters", key: "SECONDARY_CONTROL") {
        		attributeState "power", label:'Water remaining: ${currentValue} liters', icon: "http://cdn.device-icons.smartthings.com/Bath/bath6-icn@2x.png"
            }    
        }
 		valueTile("lastUpdated", "device.lastUpdated", inactiveLabel: false, decoration: "flat", width: 6, height: 2) {
    			state "default", label:'Last Updated ${currentValue}', backgroundColor:"#ffffff"
		}
    }
    
    preferences {
        input name: "height", type: "number", title: "Height", description: "Enter height of tank in cm", required: true
        input name: "diameter", type: "number", title: "Diameter", description: "Enter diameter of tank", required: true
    }
}

def parse(String description) {
    log.debug "parse(${description}) called"
	def parts = description.split(" ")
    def name  = parts.length>0?parts[0].trim():null
    def value = parts.length>1?parts[1].trim():null
    if (name && value) {
        double sensorValue = value as float
        double capacityValue = 100 - (sensorValue/height * 100 )
        if(capacityValue != 100)
        {            
            /*added by CSC - start*/
            //how much % left based on the height
            capacityValue = (height - sensorValue) / height *100
            capacityValue = capacityValue.round(2)
           	/*added by CSC - end*/
            
            sendEvent(name: name, value: capacityValue)
        }
        
        def volume = 3.14159 * (diameter/2) * (diameter/2) * height
        double capacityLiters = volume / 1000 * 2
        
        /*added by CSC -- start*/
        //calculate how much capacity left
        capacityLiters = capacityLiters * capacityValue / 100
        /*added by CSC - end*/
        
        capacityLiters = capacityLiters.round(2)
        sendEvent(name: "liters", value: capacityLiters)
        
        // Update lastUpdated date and time
        def nowDay = new Date().format("MMM dd", location.timeZone)
        def nowTime = new Date().format("h:mm a", location.timeZone)
        sendEvent(name: "lastUpdated", value: nowDay + " at " + nowTime, displayed: false)
    }
    else {
    	log.debug "Missing either name or value.  Cannot parse!"
    }
}

def installed() {

}
