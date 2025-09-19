async function loadCartItems() {
  const popup = new Notification();
  const container = document.getElementById("cart-item-container");

  try {
    const response = await fetch("LoadCartItems");
    if (!response.ok) {
      popup.error({ message: "Cart Items loading failed..." });
      return;
    }

    const json = await response.json();

    // Clear current rows
    container.innerHTML = "";

    if (!json.status) {
      popup.error({ message: json.message || "Your cart is empty..." });
      // reset totals
      document.getElementById("order-total-quantity").textContent = "0";
      document.getElementById("order-total-amount").textContent = "0.00";
      return;
    }

    let total = 0;
    let totalQty = 0;

    const frag = document.createDocumentFragment();

    json.cartItems.forEach((cart) => {
      const productSubTotal = cart.product.price * cart.qty;
      total += productSubTotal;
      totalQty += cart.qty;

      const tr = document.createElement("tr");
      tr.className = "cart-item-row";
      tr.dataset.rowId = cart.id || ("guest-" + cart.product.id);

      tr.innerHTML = `
       <td class="product-remove">
  <button type="button"
          class="tt-delete-btn remove-item"
          title="Delete"
          data-cart-id="${cart.id || ""}"
          data-product-id="${cart.product.id}">
    🗑️ <span>Delete</span>
  </button>
</td>

        <td class="product-thumbnail">
          <a href="#">
            <img src="product-images\\${cart.product.id}\\image1.jpg" alt="Product">
          </a>
        </td>
        <td class="product-title"><a href="#">${cart.product.title}</a></td>
        <td class="product-price" data-title="Price">
          <span class="currency-symbol">Rs. </span>
          <span>${new Intl.NumberFormat("en-US", { minimumFractionDigits: 2 }).format(cart.product.price)}</span>
        </td>
        <td class="product-quantity" data-title="Qty">
          <div class="pro-qty">
            <input type="number" class="quantity-input" value="${cart.qty}">
          </div>
        </td>
        <td class="product-subtotal" data-title="Subtotal">
          <span class="currency-symbol">Rs. </span>
          <span>${new Intl.NumberFormat("en-US", { minimumFractionDigits: 2 }).format(productSubTotal)}</span>
        </td>
      `;
      frag.appendChild(tr);
    });

    container.appendChild(frag);

    // Totals
    document.getElementById("order-total-quantity").textContent = totalQty;
    document.getElementById("order-total-amount").textContent = new Intl.NumberFormat("en-US", {
      minimumFractionDigits: 2
    }).format(total);

    // Bind remove click ONCE
    if (!container.dataset.removeBound) {
      container.addEventListener("click", async (e) => {
        const btn = e.target.closest(".remove-item");
        if (!btn) return;
        e.preventDefault();

        const cartId = (btn.dataset.cartId || "").trim();
        const productId = (btn.dataset.productId || "").trim();

        try {
          const body = new URLSearchParams();
          if (cartId) body.append("cartId", cartId);        // logged-in cart row
          if (productId) body.append("productId", productId); // guest session item

          const res = await fetch("RemoveCartItem", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: body.toString()
          });

          if (!res.ok) {
            popup.error({ message: "Failed to remove item. Try again." });
            return;
          }

          const jr = await res.json();
          if (jr.status) {
            popup.success({ message: "Item removed" });
            await loadCartItems(); // re-render and update totals
          } else {
            popup.error({ message: jr.message || "Could not remove item." });
          }
        } catch (_) {
          popup.error({ message: "Network error. Please try again." });
        }
      });

      container.dataset.removeBound = "1";
    }
  } catch (_) {
    popup.error({ message: "Cart Items loading failed..." });
  }
}
