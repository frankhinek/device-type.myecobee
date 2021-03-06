/**
 *  ecobeeControlPlug
 *
 *  Copyright 2014 Yves Racine
 *  LinkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
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
 */
definition(
    name: "ecobeeControlPlug",
    namespace: "yracine",
    author: "Yves Racine",
    description: "Control a plug attached to an ecobee device",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)


preferences {
    
    section("For this Ecobee thermostat") {
        input "ecobee", "capability.thermostat", title: "Ecobee Thermostat"
    }
    section("Control this SmartPlug Name") { 
        input "plugName", "text", title: "SmartPlug Name"
    }        
    section("Target control State") {
        input "plugState", "enum", title: "Control State?",metadata:[values:["on", "off", "resume",]]
    }
    section("Hold Type") {
        input "givenHoldType", "enum", title: "Hold Type?",metadata:[values:["dateTime", "nextTransition", "indefinite"]]
    }
    section("For 'dateTime' holdType, Start date for the hold (format = DD-MM-YYYY)") {
        input "givenStartDate", "text", title: "Beginning Date", required: false
    }        
    section("For 'dateTime' holdType, Start time for the hold (HH:MM,24HR)") {
        input "givenStartTime", "text", title: "Beginning time", required: false
    }        
    section("For 'dateTime' holdType, End date for the hold (format = DD-MM-YYYY)") {
        input "givenEndDate", "text", title: "End Date", required: false
    }        
    section("For 'dateTime' holdType, End time for the hold (HH:MM,24HR)" ) {
        input "givenEndTime", "text", title: "End time", required: false
    }        
    

}


def installed() {
    
    ecobee.poll()
    subscribe(app, appTouch)

}


def updated() {
    
    
    ecobee.poll()
    subscribe(app, appTouch)


}

def appTouch(evt) {
    log.debug "ecobeeControlPlug> about to take actions"
    def plugSettings = [holdType:"${givenHoldType}"]
  

    if (givenHoldType == "dateTime") {
    
        if ((givenStartDate ==null) || (givenEndDate == null) || (givenStartTime ==null) || (givenEndTime==null)) {
        
          send("ecobeeControlPlug>holdType=dateTime and dates/times are not valid for controlling plugName ${plugName}")
          return 
        }
        plugSettings = [holdType:"dateTime",startDate:"${givenStartDate}",startTime:"${givenStartTime}",endDate:"${givenEndDate}",endTime:"${givenEndTime}"]         
    }
    log.debug( "About to call controlPlug for thermostatId=${thermostatId}, plugName=${plugName}")
    ecobee.controlPlug(null, plugName, plugState, plugSettings)
}


private send(msg) {
    if ( sendPushMessage != "No" ) {
        log.debug( "sending push message" )
        sendPush( msg )
    }

    if ( phoneNumber ) {
        log.debug( "sending text message" )
        sendSms( phoneNumber, msg )
    }

    log.debug msg
}
