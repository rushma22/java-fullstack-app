async function verifyAccount() {
    const code = document.getElementById("verificationCode").value;

    const codeObj = {
        code: code
    }

    const codeJson = JSON.stringify(codeObj);

    const response = await fetch(
            "VerifyAccount",
            {
                method: "POST",
                body: codeJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
                    
    );
    
    if(response.ok){
         const responseJson = await response.json();
         console.log(responseJson);
        if(responseJson.status){
            window.location = "index.html"
        }else{
            if(responseJson.message == "Email not found"){
                window.location = "sign-up.html"
            }else{
                 document.getElementById("message").innerHTML = responseJson.message;
            }
           
        }
    }else{
        document.getElementById("message").innerHTML = "Verification failed.Please try again"
    }
}