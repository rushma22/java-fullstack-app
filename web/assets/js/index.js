function indexOnloadFunctions() {
    console.log("indexOnloadFunctions: Start");
    checkSessionCart();
    loadProductData();
}

// ✅ CHECK SESSION CART
async function checkSessionCart() {
    console.log("checkSessionCart: Start");
    const popup = new Notification(); // Renamed to avoid conflict
    try {
        const response = await fetch("CheckSessionCart");
        console.log("checkSessionCart: Response received", response);
        if (!response.ok) {
            console.error("checkSessionCart: Response not OK");
            popup.error({
                message: "Something went wrong! Try again shortly"
            });
        } else {
            console.log("checkSessionCart: Response OK");
        }
    } catch (error) {
        console.error("checkSessionCart: Fetch error", error);
        popup.error({
            message: "Something went wrong! Try again shortly"
        });
    }
}

// ✅ LOAD PRODUCT DATA
async function loadProductData() {
    console.log("loadProductData: Start");
    const popup = new Notification();
    try {
        const response = await fetch("LoadHomeData");
        console.log("loadProductData: Response received", response);

        if (response.ok) {
            const json = await response.json();
            console.log("loadProductData: JSON parsed", json);

            if (json.status) {
                console.log("loadProductData: Status true");
                loadBrands(json);
                loadNewArrivals(json);
            } else {
                console.error("loadProductData: Status false");
                popup.error({
                    message: "Something went wrong! Try again shortly"
                });
            }
        } else {
            console.error("loadProductData: Response not OK");
            popup.error({
                message: "Something went wrong! Try again shortly"
            });
        }
    } catch (error) {
        console.error("loadProductData: Fetch error", error);
        popup.error({
            message: "Something went wrong! Try again shortly"
        });
    }
}

// ✅ LOAD BRANDS
function loadBrands(json) {
    console.log("loadBrands: Start");

    const product_brand_container = document.getElementById("product-brand-container");
    const product_brand_card = document.getElementById("product-brand-card");

    if (!product_brand_card || !product_brand_container) {
        console.error("Brand template or container not found.");
        return;
    }

    product_brand_container.innerHTML = "";
    let card_delay = 200;

    json.brandList.forEach((item, index) => {
        console.log(`loadBrands: Rendering brand ${index + 1}`, item);

        const cardClone = product_brand_card.cloneNode(true);
        const miniCard = cardClone.querySelector("#product-brand-mini-card");
        const brandLink = cardClone.querySelector("#product-brand-a");
        const brandTitle = cardClone.querySelector("#product-brand-title");

        if (miniCard) {
            miniCard.setAttribute("data-sal", "zoom-out");
            miniCard.setAttribute("data-sal-delay", String(card_delay));
        }
        if (brandLink) brandLink.href = "search.html";
        if (brandTitle) brandTitle.textContent = item.name;

        cardClone.style.display = "block";
        product_brand_container.appendChild(cardClone);

        card_delay += 100;
    });

    // ✅ Call once after all brands are added
    if (typeof sal === "function") {
        sal();
    }
}

// ✅ LOAD NEW ARRIVALS
function loadNewArrivals(json) {
    console.log("loadNewArrivals: Start");

    const container = document.getElementById("new-arrival-product-container");
    if (!container) {
        console.error("Product container not found.");
        return;
    }

    container.innerHTML = "";

    json.productList.forEach((item, index) => {
        console.log(`loadNewArrivals: Rendering product ${index + 1}`, item);

        const card = `
        <div class="product-card">
            <div class="thumbnail">
                <a href="single-product.html?id=${item.id}">
                    <img src="product-images/${item.id}/image1.jpg" alt="Product Image" class="main-img" loading="lazy">
                    <img src="product-images/${item.id}/image2.jpg" alt="Product Image" class="hover-img">
                </a>
            </div>
            <div class="details">
                <h4>${item.title}</h4>
                <div class="price">Rs. ${new Intl.NumberFormat("en-US", { minimumFractionDigits: 2 }).format(item.price)}</div>
                <div class="color-dot" style="background-color: ${item.color?.value || '#000'};"></div>
            </div>
            <div class="actions">
                <a href="single-product.html?id=${item.id}">View</a>
                <a onclick="addToCart(${item.id}, 1)">Add to Cart</a>
            </div>
        </div>`;
        container.innerHTML += card;
    });
}

// ✅ ADD TO CART
async function addToCart(productId, qty) {
    console.log("addToCart: Start - productId:", productId, "qty:", qty);
    const popup = new CustomNotification();

    try {
        const response = await fetch(`AddToCart?prId=${productId}&qty=${qty}`);
        console.log("addToCart: Response received", response);

        if (response.ok) {
            const json = await response.json();
            console.log("addToCart: JSON parsed", json);

            if (json.status) {
                popup.success({ message: json.message });
            } else {
                popup.error({ message: "Something went wrong. Try again" });
            }
        } else {
            popup.error({ message: "Something went wrong. Try again" });
        }
    } catch (error) {
        console.error("addToCart: Fetch error", error);
        popup.error({ message: "Something went wrong. Try again" });
    }
}
