/**
 * Autocomplete và Suggestions Library
 * Cung cấp các tính năng gợi ý cho forms
 */

class AutocompleteManager {
    constructor() {
        this.cache = new Map();
        this.debounceTimers = new Map();
        this.activeRequests = new Map();
    }

    /**
     * Khởi tạo autocomplete cho input
     */
    initAutocomplete(inputId, apiEndpoint, options = {}) {
        const input = document.getElementById(inputId);
        if (!input) {
            console.warn(`Input với ID '${inputId}' không tồn tại`);
            return;
        }

        const config = {
            minLength: 2,
            maxResults: 10,
            debounceDelay: 300,
            cacheEnabled: true,
            showNoResults: true,
            customRenderer: null,
            onSelect: null,
            queryParams: {},
            ...options
        };

        this.setupInputEvents(input, apiEndpoint, config);
        this.createSuggestionContainer(input);
    }

    /**
     * Setup event listeners cho input
     */
    setupInputEvents(input, apiEndpoint, config) {
        const inputId = input.id;

        input.addEventListener('input', (e) => {
            this.handleInput(e.target, apiEndpoint, config);
        });

        input.addEventListener('keydown', (e) => {
            this.handleKeyDown(e, inputId);
        });

        input.addEventListener('blur', (e) => {
            // Delay để cho phép click vào suggestion
            setTimeout(() => this.hideSuggestions(inputId), 150);
        });

        input.addEventListener('focus', (e) => {
            const value = e.target.value.trim();
            if (value.length >= config.minLength) {
                this.showCachedSuggestions(inputId, value);
            }
        });
    }

    /**
     * Xử lý input change với debouncing
     */
    handleInput(input, apiEndpoint, config) {
        const inputId = input.id;
        const query = input.value.trim();

        // Clear previous timer
        if (this.debounceTimers.has(inputId)) {
            clearTimeout(this.debounceTimers.get(inputId));
        }

        // Hide suggestions if query too short
        if (query.length < config.minLength) {
            this.hideSuggestions(inputId);
            return;
        }

        // Debounce API call
        const timer = setTimeout(() => {
            this.fetchSuggestions(inputId, query, apiEndpoint, config);
        }, config.debounceDelay);

        this.debounceTimers.set(inputId, timer);
    }

    /**
     * Fetch suggestions từ API
     */
    async fetchSuggestions(inputId, query, apiEndpoint, config) {
        // Cancel previous request
        if (this.activeRequests.has(inputId)) {
            this.activeRequests.get(inputId).abort();
        }

        // Check cache first
        const cacheKey = `${apiEndpoint}_${query.toLowerCase()}`;
        if (config.cacheEnabled && this.cache.has(cacheKey)) {
            this.displaySuggestions(inputId, this.cache.get(cacheKey), config);
            return;
        }

        try {
            const controller = new AbortController();
            this.activeRequests.set(inputId, controller);

            // Build URL with query params
            const url = new URL(apiEndpoint, window.location.origin);
            url.searchParams.append('q', query);
            
            Object.entries(config.queryParams).forEach(([key, value]) => {
                if (value !== null && value !== undefined) {
                    url.searchParams.append(key, value);
                }
            });

            const response = await fetch(url, {
                signal: controller.signal,
                headers: {
                    'Accept': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            const suggestions = await response.json();
            
            // Cache results
            if (config.cacheEnabled) {
                this.cache.set(cacheKey, suggestions);
            }

            this.displaySuggestions(inputId, suggestions, config);

        } catch (error) {
            if (error.name !== 'AbortError') {
                console.warn('Autocomplete fetch error:', error);
                this.displaySuggestions(inputId, [], config);
            }
        } finally {
            this.activeRequests.delete(inputId);
        }
    }

    /**
     * Hiển thị suggestions
     */
    displaySuggestions(inputId, suggestions, config) {
        const container = this.getSuggestionContainer(inputId);
        if (!container) return;

        container.innerHTML = '';

        if (suggestions.length === 0) {
            if (config.showNoResults) {
                const noResults = document.createElement('div');
                noResults.className = 'autocomplete-no-results';
                noResults.textContent = 'Không tìm thấy kết quả';
                container.appendChild(noResults);
            }
            container.style.display = 'none';
            return;
        }

        const limitedSuggestions = suggestions.slice(0, config.maxResults);
        
        limitedSuggestions.forEach((suggestion, index) => {
            const item = this.createSuggestionItem(suggestion, index, config);
            item.addEventListener('click', (e) => {
                e.preventDefault();
                this.selectSuggestion(inputId, suggestion, config);
            });
            container.appendChild(item);
        });

        container.style.display = 'block';
        this.positionContainer(inputId, container);
    }

    /**
     * Tạo suggestion item
     */
    createSuggestionItem(suggestion, index, config) {
        const item = document.createElement('div');
        item.className = 'autocomplete-item';
        item.setAttribute('data-index', index);

        if (config.customRenderer) {
            item.innerHTML = config.customRenderer(suggestion);
        } else {
            if (typeof suggestion === 'string') {
                item.textContent = suggestion;
            } else if (suggestion.tenNV) {
                // Trainer suggestion
                item.innerHTML = `
                    <div class="suggestion-name">${suggestion.tenNV}</div>
                    <div class="suggestion-detail">${suggestion.maNV} - ${suggestion.email}</div>
                `;
            } else {
                item.textContent = suggestion.toString();
            }
        }

        return item;
    }

    /**
     * Select suggestion
     */
    selectSuggestion(inputId, suggestion, config) {
        const input = document.getElementById(inputId);
        if (!input) return;

        const value = typeof suggestion === 'string' ? suggestion : 
                     suggestion.tenNV || suggestion.toString();
        
        input.value = value;
        this.hideSuggestions(inputId);

        // Trigger change event
        input.dispatchEvent(new Event('change', { bubbles: true }));

        // Call custom callback
        if (config.onSelect) {
            config.onSelect(suggestion, input);
        }
    }

    /**
     * Handle keyboard navigation
     */
    handleKeyDown(e, inputId) {
        const container = this.getSuggestionContainer(inputId);
        if (!container || container.style.display === 'none') return;

        const items = container.querySelectorAll('.autocomplete-item');
        const current = container.querySelector('.autocomplete-item.selected');
        let index = current ? Array.from(items).indexOf(current) : -1;

        switch (e.key) {
            case 'ArrowDown':
                e.preventDefault();
                index = Math.min(index + 1, items.length - 1);
                this.highlightItem(items, index);
                break;

            case 'ArrowUp':
                e.preventDefault();
                index = Math.max(index - 1, -1);
                this.highlightItem(items, index);
                break;

            case 'Enter':
                e.preventDefault();
                if (current) {
                    current.click();
                }
                break;

            case 'Escape':
                this.hideSuggestions(inputId);
                break;
        }
    }

    /**
     * Highlight suggestion item
     */
    highlightItem(items, index) {
        items.forEach(item => item.classList.remove('selected'));
        if (index >= 0 && index < items.length) {
            items[index].classList.add('selected');
        }
    }

    /**
     * Create suggestion container
     */
    createSuggestionContainer(input) {
        const containerId = `autocomplete-${input.id}`;
        let container = document.getElementById(containerId);
        
        if (!container) {
            container = document.createElement('div');
            container.id = containerId;
            container.className = 'autocomplete-container';
            container.style.display = 'none';
            
            // Insert after input
            input.parentNode.insertBefore(container, input.nextSibling);
        }

        return container;
    }

    /**
     * Get suggestion container
     */
    getSuggestionContainer(inputId) {
        return document.getElementById(`autocomplete-${inputId}`);
    }

    /**
     * Position container relative to input
     */
    positionContainer(inputId, container) {
        const input = document.getElementById(inputId);
        if (!input) return;

        const rect = input.getBoundingClientRect();
        
        container.style.position = 'absolute';
        container.style.top = `${rect.bottom + window.scrollY}px`;
        container.style.left = `${rect.left + window.scrollX}px`;
        container.style.width = `${rect.width}px`;
        container.style.zIndex = '1000';
    }

    /**
     * Hide suggestions
     */
    hideSuggestions(inputId) {
        const container = this.getSuggestionContainer(inputId);
        if (container) {
            container.style.display = 'none';
        }
    }

    /**
     * Show cached suggestions
     */
    showCachedSuggestions(inputId, query) {
        // Implementation for showing cached results
        // This is a simplified version
    }

    /**
     * Clear cache
     */
    clearCache() {
        this.cache.clear();
    }

    /**
     * Destroy autocomplete for input
     */
    destroy(inputId) {
        const container = this.getSuggestionContainer(inputId);
        
        if (container) {
            container.remove();
        }

        if (this.debounceTimers.has(inputId)) {
            clearTimeout(this.debounceTimers.get(inputId));
            this.debounceTimers.delete(inputId);
        }

        if (this.activeRequests.has(inputId)) {
            this.activeRequests.get(inputId).abort();
            this.activeRequests.delete(inputId);
        }
    }
}

// Global instance
const autocompleteManager = new AutocompleteManager();

// Utility functions for easy setup
function setupKhachHangAutocomplete(inputId, options = {}) {
    autocompleteManager.initAutocomplete(inputId, '/api/autocomplete/khachhang', {
        placeholder: 'Nhập tên khách hàng...',
        ...options
    });
}

function setupNhanVienAutocomplete(inputId, loaiNV = null, options = {}) {
    autocompleteManager.initAutocomplete(inputId, '/api/autocomplete/nhanvien', {
        queryParams: { loaiNV },
        placeholder: 'Nhập tên nhân viên...',
        ...options
    });
}

function setupEmailAutocomplete(inputId, options = {}) {
    autocompleteManager.initAutocomplete(inputId, '/api/autocomplete/email', {
        minLength: 3,
        placeholder: 'Nhập email...',
        ...options
    });
}

function setupDiaChiAutocomplete(inputId, options = {}) {
    autocompleteManager.initAutocomplete(inputId, '/api/autocomplete/diachi', {
        minLength: 3,
        placeholder: 'Nhập địa chỉ...',
        ...options
    });
}

function setupTrainerAutocomplete(inputId, maBM = null, options = {}) {
    autocompleteManager.initAutocomplete(inputId, '/api/autocomplete/trainer', {
        queryParams: { maBM },
        placeholder: 'Nhập tên trainer...',
        customRenderer: (trainer) => `
            <div class="trainer-suggestion">
                <div class="trainer-name">${trainer.tenNV}</div>
                <div class="trainer-details">${trainer.maNV} - ${trainer.email}</div>
            </div>
        `,
        ...options
    });
}

// Auto-initialize common autocomplete fields
document.addEventListener('DOMContentLoaded', function() {
    // Auto-setup for common field names
    const khachHangInputs = document.querySelectorAll('input[name*="khachHang"], input[id*="khachHang"], input[placeholder*="tên khách hàng"]');
    khachHangInputs.forEach(input => {
        if (input.id && input.dataset.autocomplete !== 'false') {
            setupKhachHangAutocomplete(input.id);
        }
    });

    const emailInputs = document.querySelectorAll('input[type="email"], input[name*="email"], input[id*="email"]');
    emailInputs.forEach(input => {
        if (input.id && input.dataset.autocomplete !== 'false') {
            setupEmailAutocomplete(input.id);
        }
    });

    const addressInputs = document.querySelectorAll('input[name*="diaChi"], input[id*="diaChi"], input[placeholder*="địa chỉ"]');
    addressInputs.forEach(input => {
        if (input.id && input.dataset.autocomplete !== 'false') {
            setupDiaChiAutocomplete(input.id);
        }
    });
}); 