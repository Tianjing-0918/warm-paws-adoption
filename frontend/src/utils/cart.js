const KEY = 'warmpaws_cart'

export function readCart() {
  try {
    return JSON.parse(localStorage.getItem(KEY) || '[]')
  } catch (e) {
    return []
  }
}

export function writeCart(items) {
  localStorage.setItem(KEY, JSON.stringify(items))
  window.dispatchEvent(new CustomEvent('cart-updated', { detail: items }))
}

export function addToCart(product, quantity = 1) {
  const items = readCart()
  const found = items.find(i => i.id === product.id)
  if (found) found.quantity = Math.min((found.stock || 99), found.quantity + quantity)
  else {
    items.push({
      id: product.id,
      name: product.name,
      price: Number(product.price),
      image: product.image,
      stock: product.stock || 99,
      quantity
    })
  }
  writeCart(items)
  return items
}

export function updateQty(id, quantity) {
  const items = readCart()
  const item = items.find(i => i.id === id)
  if (item) item.quantity = Math.max(1, Math.min(item.stock || 99, quantity))
  writeCart(items)
  return items
}

export function removeItem(id) {
  const items = readCart().filter(i => i.id !== id)
  writeCart(items)
  return items
}

export function clearCart() {
  writeCart([])
}

export function cartCount(items) {
  return (items || readCart()).reduce((n, i) => n + i.quantity, 0)
}

export function cartTotal(items) {
  return (items || readCart()).reduce((n, i) => n + i.price * i.quantity, 0)
}
