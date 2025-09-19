/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


async function getItems(){
    console.log("ok")
    
    const response = await fetch("PurchaseHistory");
    if(response.ok){
        const json = await response.json();
        console.log("jsonnn",json)
        if(json.status){
            
        }else{
            
        }
    }else{
        
    }
}