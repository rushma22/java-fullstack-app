async function adminSignIn(){
     const email = "rushytq1@gmail.com";
    const password = document.getElementById("password").value;

    const signIn = {
        email: email,
        password: password
    }

    const signInjson = JSON.stringify(signIn);

    const response = await fetch(
            "SignIn",
            {
                method: "POST",
                body: signInjson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );
    
    if(response.ok){
        const json = await response.json();
        console.log(json)
        if(json.status){
            if(json.message == "1"){
                window.location = "verify-account.html"
            }else{
                window.location = "admin.html"
                console.log("something here")
            }
        }else{
            
            document.getElementById("message").innerHTML = json.message;
            document.getElementById("message").style.display = "block";
        }
    }else{
        document.getElementById("message").innerHTML = "Sign in Failed. Please try a again";
    }
}