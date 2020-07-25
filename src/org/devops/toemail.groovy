package org.devops

//定义邮件内容
def Email(status,emailUser){
    emailext body: """
            <!DOCTYPE html> 
            <html> 
            <head> 
            <meta charset="UTF-8"> 
            </head> 
            <body leftmargin="8" marginwidth="0" topmargin="8" marginheight="4" offset="0"> 
                <img src="http://192.168.33.1:30088/root/static/-/blob/master/images/headshot.png">
                <table width="95%" cellpadding="0" cellspacing="0" style="font-size: 11pt; font-family: Tahoma, Arial, Helvetica, sans-serif">   
                    <tr> 
                        <td><br /> 
                            <b><font color="#0B610B">Build Information</font></b> 
                        </td> 
                    </tr> 
                    <tr> 
                        <td> 
                            <ul> 
                                <li>Job Name:${JOB_NAME}</li>         
                                <li>Build Id:${BUILD_ID}</li> 
                                <li>Build Status: ${status} </li>                         
                                <li>Build URL:<a href="${BUILD_URL}">${BUILD_URL}</a></li>    
                                <li>Build Log:<a href="${BUILD_URL}console">${BUILD_URL}console</a></li> 
                            </ul> 
                        </td> 
                    </tr> 
                    <tr>  
                </table> 
            </body> 
            </html>  """,
            subject: "Jenkins-${JOB_NAME}Build Information ",
            to: emailUser
        
}