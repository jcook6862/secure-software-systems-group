/**
 *  Hue B Smart White Ambiance
 *
 *  Copyright 2016 Anthony Pastor
 *
 *  Thanks to @tmleafs for his help on this addition to the Hue B Smart DTHs!
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
 *	Version 1.0 
 *
 *	Version 1.0b -- attribute colorTemp is now colorTemperature - changing colorTemperature no longer turns on device
 *
 *  Version 1.0c -- corrected name of DTH
 *
 *  Version 1.1 -- added applyRelax, applyConcentrate, applyReading, and applyEnergize functions 
 */
 
metadata {
	definition (name: "Hue B Smart White Ambiance", namespace: "info_fiend", author: "Anthony Pastor") {
		capability "Switch Level"
		capability "Actuator"
        capability "Color Temperature"
		capability "Switch"
		capability "Polling"
		capability "Refresh"
		capability "Sensor"
       	capability "Configuration"
        
        command "reset"
        command "refresh"
        command "flash"
        command "flash_off"
        command "setTransitionTime"
        command "setColorTemperature"        
        command "ttUp"
        command "ttDown"
        command "updateStatus"
		command "getHextoXY"
        command "sendToHub"
		command "applyRelax"
        command "applyConcentrate"
        command "applyReading"
        command "applyEnergize"

 		attribute "colorTemperature", "number"
		attribute "bri", "number"
		attribute "sat", "number"
        attribute "level", "number"
		attribute "reachable", "string"
		attribute "hue", "number"
		attribute "on", "string"
        attribute "transitionTime", "NUMBER"
        attribute "hueID", "STRING"
        attribute "host", "STRING"
        attribute "hhName", "STRING"
		attribute "colormode", "enum", ["XY", "CT", "HS"]
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles (scale: 2){
		multiAttributeTile(name:"rich-control", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#C6C7CC", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#C6C7CC", nextState:"turningOn"
			}
			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"switch level.setLevel", range:"(0..100)"
			}

		}

		/* reset / refresh */	
		standardTile("reset", "device.reset", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:"Reset Color", action:"reset", icon:"st.lights.philips.hue-single"
		}
		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        
        /* Color Temperature */
		valueTile("valueCT", "device.colorTemperature", inactiveLabel: false, decoration: "flat", width: 2, height: 1) {
			state "colorTemperature", label: 'Color Temp:  ${currentValue}'
        }
        controlTile("colorTemperature", "device.colorTemperature", "slider", inactiveLabel: false,  width: 4, height: 1, range:"(2200..6500)") { 
        	state "setCT", action:"setColorTemperature"
		}
        
        /* alert / flash */
		standardTile("flash", "device.flash", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:"Flash", action:"flash", icon:"st.lights.philips.hue-single"
		}
        
        /* transition time */
		valueTile("ttlabel", "transitionTime", decoration: "flat", width: 4, height: 1) {
			state "default", label:'Transition Time: ${currentValue}00ms'
		}
		valueTile("ttdown", "device.transitionTime", decoration: "flat", width: 1, height: 1) {
			state "default", label: "-", action:"ttDown"
		}
		valueTile("ttup", "device.transitionTime", decoration: "flat", width: 1, height: 1) {
			state "default", label:"+", action:"ttUp"
		}
        
        /* misc */
		valueTile("colormode", "device.colormode", inactiveLabel: false, decoration: "flat", width: 4, height: 1) {
			state "default", label: 'Colormode: ${currentValue}'
		}

        valueTile("hueID", "device.hueID", inactiveLabel: false, decoration: "flat", width: 2, height: 1) {
			state "default", label: 'ID: ${currentValue}'
		}
        valueTile("host", "device.host", inactiveLabel: false, decoration: "flat", width: 3, height: 1) {
			state "default", label: 'Host: ${currentValue}'
        }
        valueTile("username", "device.username", inactiveLabel: false, decoration: "flat", width: 3, height: 1) {
			state "default", label: 'User: ${currentValue}'
        }

	}
	main(["rich-control"])
	details(["rich-control","ttlabel","ttdown","ttup","valueCT","colorTemp","hueID","reset","refresh"])	
}

private configure() {		
    def commandData = parent.getCommandData(device.deviceNetworkId)
    log.debug "${commandData = commandData}"
    sendEvent(name: "hueID", value: commandData.deviceId, displayed:true, isStateChange: true)
    sendEvent(name: "host", value: commandData.ip, displayed:false, isStateChange: true)
    sendEvent(name: "username", value: commandData.username, displayed:false, isStateChange: true)
}


// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def ttUp() {
	log.trace "Hue B Smart Ambience Bulb: ttUp(): "
    
	def tt = this.device.currentValue("transitionTime") ?: 0
    if (tt == null) { tt = 4 }
    sendEvent(name: "transitionTime", value: tt + 1)
}

def ttDown() {
	log.trace "Hue B Smart Ambience Bulb: ttDown(): "
    
	def tt = this.device.currentValue("transitionTime") ?: 0
    tt = tt - 1
    if (tt < 0) { tt = 0 }
    sendEvent(name: "transitionTime", value: tt)
}

/** 
 * capability.switchLevel 
 **/

def setLevel(inLevel) {
	log.trace "Hue B Smart Ambience Bulb: setLevel( ${inLevel} ): "
    
    def level = parent.scaleLevel(inLevel, true, 254)
    def commandData = parent.getCommandData(device.deviceNetworkId)    
    def tt = this.device.currentValue("transitionTime") ?: 0
    
	parent.sendHubCommand(new physicalgraph.device.HubAction(
    	[
        	method: "PUT",
			path: "/api/${commandData.username}/lights/${commandData.deviceId}/state",
	        headers: [
	        	host: "${commandData.ip}"
			],
	        body: [on: true, bri: level, transitiontime: tt]
		])
	)    
}


def sendToHub(values) {
	log.trace "Hue B Smart Ambience Bulb: sendToHub ( ${values} ): "
    
	def validValues = [:]
	def commandData = parent.getCommandData(device.deviceNetworkId)
    def sendBody = [:]

	if (values.level) {
    	def bri = values.level 
    	validValues.bri = parent.scaleLevel(bri, true, 254)
        sendBody["bri"] = validValues.bri
		if (values.level > 0) { 
        	sendBody["on"] = true
        } else {
        	sendBody["on"] = false
		}            
	}

	if (values.switch == "off" ) {
    	sendBody["on"] = false
    } else if (values.switch == "on") {
		sendBody["on"] = true
	}
        
    sendBody["transitiontime"] = device.currentValue("transitionTime") as Integer ?: 0
    
	if (values.hue || values.saturation ) {
		def hue = values.hue ?: this.device.currentValue("hue")
    	validValues.hue = parent.scaleLevel(hue, true, 65535)
		sendBody["hue"] = validValues.hue
		def sat = values.saturation ?: this.device.currentValue("saturation")
	    validValues.saturation = parent.scaleLevel(sat, true, 254)
		sendBody["sat"] = validValues.saturation
        
	}
    
    log.debug "Sending ${sendBody} "

	parent.sendHubCommand(new physicalgraph.device.HubAction(
    	[
        	method: "PUT",
			path: "/api/${commandData.username}/lights/${commandData.deviceId}/state",
	       	headers: [
	   	    	host: "${commandData.ip}"
			],
	        body: sendBody 
		])
	)    
	sendEvent(name: "colormode", value: "HS") //, isStateChange: true) 
    sendEvent(name: "hue", value: values.hue) //, isStateChange: true) 
    sendEvent(name: "saturation", value: values.saturation, isStateChange: true) 
    
}

def setHue(inHue) {
	log.debug "Hue B Smart Ambience Bulb: setHue()."
        
	def sat = this.device.currentValue("saturation") ?: 100
	sendToHub([saturation:sat, hue:inHue])

}

def setSaturation(inSat) {
	log.debug "Hue B Smart Ambience Bulb: setSaturation( ${inSat} )."

    def hue = this.device.currentValue("hue") ?: 100
	sendToHub([saturation:inSat, hue: hue])
    
}


/**
 * capability.colorTemperature 
**/

def setColorTemperature(inCT) {
	log.debug("Hue B Smart Ambience Bulb: setColorTemperature ( ${inCT} )")
    
    def colorTemp = inCT ?: this.device.currentValue("colorTemperature")
    colorTemp = Math.round(1000000/colorTemp)    
	def commandData = parent.getCommandData(device.deviceNetworkId)
    def tt = device.currentValue("transitionTime") as Integer ?: 0
    
    
	parent.sendHubCommand(new physicalgraph.device.HubAction(
    	[
        	method: "PUT",
			path: "/api/${commandData.username}/lights/${commandData.deviceId}/state",
	        headers: [
	        	host: "${commandData.ip}"
			],
	        body: [ct: colorTemp, transitiontime: tt]
		])
	)
}

def applyRelax() {
	log.info "applyRelax"
	setColorTemperature(2141)
}

def applyConcentrate() {
	log.info "applyConcentrate"
    setColorTemperature(4329)
}

def applyReading() {
	log.info "applyReading"
    setColorTemperature(2890)
}

def applyEnergize() {
	log.info "applyEnergize"
    setColorTemperature(6410)
}


/** 
 * capability.switch
 **/
def on() {
	log.debug "Hue B Smart Ambience: on()"

    def commandData = parent.getCommandData(device.deviceNetworkId)    
	def tt = device.currentValue("transitionTime") as Integer ?: 0
    def percent = device.currentValue("level") as Integer ?: 100
    def level = parent.scaleLevel(percent, true, 254)
    
        return new physicalgraph.device.HubAction(
    	[
        	method: "PUT",
			path: "/api/${commandData.username}/lights/${commandData.deviceId}/state",
	        headers: [
	        	host: "${commandData.ip}"
			],
	        body: [on: true, bri: level, transitiontime: tt]
		])
}

def off() {
	log.debug "Hue B Smart Ambience Bulb: off()"
    def commandData = parent.getCommandData(device.deviceNetworkId)
    def tt = device.currentValue("transitionTime") as Integer ?: 0
    
    //parent.sendHubCommand(
    return new physicalgraph.device.HubAction(
    	[
        	method: "PUT",
			path: "/api/${commandData.username}/lights/${commandData.deviceId}/state",
	        headers: [
	        	host: "${commandData.ip}"
			],
	        body: [on: false, transitiontime: tt]
		])
}

/** 
 * capability.polling
 **/
def poll() {
	refresh()
}

/**
 * capability.refresh
 **/
def refresh() {
	log.debug "Hue B Smart Ambience Bulb: refresh()."
	parent.doDeviceSync()
    configure()
}

def reset() {
	log.debug "Hue B Smart Ambience Bulb: reset()."
    sendToHub ([level: 100, sat: 56, hue: 23])
}

/**
 * capability.alert (flash)
 **/
def flash() {
	log.debug "Hue B Smart Ambience Bulb: flash()"
    def commandData = parent.getCommandData(device.deviceNetworkId)
	parent.sendHubCommand(new physicalgraph.device.HubAction(
    	[
        	method: "PUT",
			path: "/api/${commandData.username}/lights/${commandData.deviceId}/state",
	        headers: [
	        	host: "${commandData.ip}"
			],
	        body: [alert: "lselect"]
		])
	)
    
    runIn(5, flash_off)
}

def flash_off() {
	log.debug "Hue B Smart Ambience Bulb: flash_ off()"
    def commandData = parent.getCommandData(device.deviceNetworkId)
	parent.sendHubCommand(new physicalgraph.device.HubAction(
    	[
        	method: "PUT",
			path: "/api/${commandData.username}/lights/${commandData.deviceId}/state",
	        headers: [
	        	host: "${commandData.ip}"
			],
	        body: [alert: "none"]
		])
	)
}


/**
 * Update Status
 **/

def updateStatus(action, param, val) {
	log.debug "Hue B Smart Ambience Bulb: updateStatus: ${param}:${val}"
	if (action == "state") {
		switch(param) {
        	case "on":
            	def onoff
            	if (val == true) {
                	sendEvent(name: "switch", value: on, isStateChange: true)                	     
                
                } else {
	            	sendEvent(name: "switch", value: off)
                	sendEvent(name: "effect", value: "none", isStateChange: true)    
                }    
                break

			case "bri":
            	sendEvent(name: "level", value: parent.scaleLevel(val)) //parent.scaleLevel(val, true, 255))
                break
			case "hue":
            	sendEvent(name: "hue", value: parent.scaleLevel(val)) //parent.scaleLevel(val, false, 65535))
			    sendEvent(name: "colormode", value: "HS", isStateChange: true)                 
                break
            case "sat":
            	sendEvent(name: "sat", value: parent.scaleLevel(val)) //parent.scaleLevel(val))
			    sendEvent(name: "colormode", value: "HS", isStateChange: true)                 
                break
			case "ct": 
            	sendEvent(name: "colorTemperature", value: Math.round(1000000/val))  //Math.round(1000000/val))
                sendEvent(name: "colormode", value: "CT", isStateChange: true) 
                break
			case "reachable":
				sendEvent(name: "reachable", value: val, isStateChange: true)
				break
            case "transitiontime":
            	sendEvent(name: "transitionTime", value: val, isStateChange: true)
                break
			case "alert":
            	if (val == "none") {
            		flash_off() 	//sendEvent(name: "alert", value: val, isStateChange: true)
                } else if (val == "lselect") {
                	flash_on()
                }
                break
    
			default: 
				log.debug("Unhandled parameter: ${param}. Value: ${val}")    
        }
    }
}

def getDeviceType() { return "lights" }