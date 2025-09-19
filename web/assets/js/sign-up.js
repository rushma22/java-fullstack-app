async function signUp() {

    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const user = {
        firstName: firstName,
        lastName: lastName,
        email: email,
        password: password,

    }

    const userJson = JSON.stringify(user);
    console.log(userJson)
    const response = await fetch(
            "SignUp",
            {
                method: "POST",
                body: userJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) { //status is 200 then it will return ok / true
        const responseJson = await response.json();
        console.log(responseJson);
//      console.log(response);

        if (responseJson.status) {
//            document.getElementById("message").innerHTML = responseJson.message;
//            document.getElementById("message").className = "text-success";
            window.location = "verify-account.html"
        } else {
           document.getElementById("message").innerHTML = responseJson.message;
              document.getElementById("message").style.display = "block";
        }

    } else {
         document.getElementById("message").innerHTML = "Registration failed. Please try again";
    }

}

