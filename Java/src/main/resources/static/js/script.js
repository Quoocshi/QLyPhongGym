function scrollRow(rowId, direction) {
    const row = document.getElementById(rowId);
    const scrollAmount = 432; // 400px thẻ + 32px gap
    row.scrollBy({ left: direction * scrollAmount, behavior: 'smooth' });
}