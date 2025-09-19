function loadData() {
    getUserData();
    getCityData();
}

async function getUserData() {
    const response = await fetch("MyAccount");
   if (response.ok) {
//        console.log(await response.json())
        const json = await response.json();
        console.log("json plss",json)
//        document.getElementById("userName").innerHTML = `Hello ${json.firstName} ${json.lastName}`
//        document.getElementById("since").innerHTML = `Smart Trade Member Since ${json.since}`
        document.getElementById("firstName").value = json.firstName;
        document.getElementById("lastName").value = json.lastName;
        document.getElementById("currentPassword").value = json.password;
        
        if(json.addressList != undefined){
            document.getElementById("lineOne").value = json.addressList[0].lineOne;
            document.getElementById("lineTwo").value = json.addressList[0].lineTwo;
            document.getElementById("postalCode").value = json.addressList[0].postalCode;
            document.getElementById("citySelect").value = json.addressList[0].city.id;
            
//            document.getElementById("addName").value = `Name : ${json.firstName} ${json.lastName} ` ;
//            document.getElementById("addEmail").value = `Email : ${json.addressList[0].user.email} ` ;
        }
    } else {
        console.log("failedd")
    }

}

async function getCityData() {
    const response = await fetch("CityData");
    if (response.ok) {
        const json = await response.json();
        const citySelect = document.getElementById("citySelect");
        json.forEach(city => {
            let option = document.createElement("option");
            option.innerHTML = city.name;
            option.value = city.id;
            citySelect.appendChild(option);
        });

    }
}

async function saveChanges() {


    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const lineOne = document.getElementById("lineOne").value;
    const lineTwo = document.getElementById("lineTwo").value;
    const postalCode = document.getElementById("postalCode").value;
    const cityId = document.getElementById("citySelect").value;
    const currentPassword = document.getElementById("currentPassword").value;
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    const userDataObject = {
        firstName: firstName,
        lastName: lastName,
        lineOne: lineOne,
        lineTwo: lineTwo,
        postalCode: postalCode,
        cityId: cityId,
        currentPassword: currentPassword,
        newPassword: newPassword,
        confirmPassword: confirmPassword
    };

    const userDataJSON = JSON.stringify(userDataObject);
 const popup = Notification();
    const response = await fetch("MyAccount", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: userDataJSON
    });
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            popup.success({
                message: "Product updating success"
            })
            getUserData();
        } else {
            document.getElementById("message").innerHTML = json.message;
        }
    } else {
        document.getElementById("message").innerHTML = "Profile details update failed!";
    }
}

function openMyProducts(){
    window.location = "my-products"
}