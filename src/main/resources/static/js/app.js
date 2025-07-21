/**
 * Main JavaScript file for Shivshakti Transport Management System
 * Contains common functions and utilities
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */

// Global STMS object
window.STMS = {
    // Configuration
    config: {
        apiBaseUrl: '/stms/api',
        dateFormat: 'DD/MM/YYYY',
        timeFormat: 'HH:mm',
        currency: '₹',
        pagination: {
            defaultSize: 20,
            maxSize: 100
        }
    },
    
    // Utility functions
    utils: {
        // Format currency
        formatCurrency: function(amount) {
            if (!amount) return STMS.config.currency + '0.00';
            return STMS.config.currency + parseFloat(amount).toLocaleString('en-IN', {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            });
        },
        
        // Format date
        formatDate: function(date, format) {
            if (!date) return '';
            const d = new Date(date);
            const day = String(d.getDate()).padStart(2, '0');
            const month = String(d.getMonth() + 1).padStart(2, '0');
            const year = d.getFullYear();
            
            switch(format || STMS.config.dateFormat) {
                case 'DD/MM/YYYY':
                    return `${day}/${month}/${year}`;
                case 'MM/DD/YYYY':
                    return `${month}/${day}/${year}`;
                case 'YYYY-MM-DD':
                    return `${year}-${month}-${day}`;
                default:
                    return `${day}/${month}/${year}`;
            }
        },
        
        // Format time
        formatTime: function(time) {
            if (!time) return '';
            const t = new Date(time);
            return t.toLocaleTimeString('en-IN', {
                hour: '2-digit',
                minute: '2-digit',
                hour12: false
            });
        },
        
        // Format datetime
        formatDateTime: function(datetime) {
            if (!datetime) return '';
            return STMS.utils.formatDate(datetime) + ' ' + STMS.utils.formatTime(datetime);
        },
        
        // Validate form
        validateForm: function(formId) {
            const form = document.getElementById(formId);
            if (!form) return false;
            
            let isValid = true;
            const requiredFields = form.querySelectorAll('[required]');
            
            requiredFields.forEach(field => {
                if (!field.value.trim()) {
                    field.classList.add('is-invalid');
                    isValid = false;
                } else {
                    field.classList.remove('is-invalid');
                }
            });
            
            return isValid;
        },
        
        // Show loading
        showLoading: function(message) {
            const loadingHtml = `
                <div class="text-center p-4">
                    <div class="spinner-border text-primary mb-3" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                    <p class="mb-0">${message || 'Loading...'}</p>
                </div>
            `;
            
            if ($('#loadingModal').length) {
                $('#loadingModal .modal-body').html(loadingHtml);
                $('#loadingModal').modal('show');
            }
        },
        
        // Hide loading
        hideLoading: function() {
            if ($('#loadingModal').length) {
                $('#loadingModal').modal('hide');
            }
        },
        
        // Show notification
        showNotification: function(message, type, duration) {
            type = type || 'info';
            duration = duration || 5000;
            
            const alertClass = `alert-${type}`;
            const iconClass = {
                'success': 'fa-check-circle',
                'danger': 'fa-exclamation-circle',
                'warning': 'fa-exclamation-triangle',
                'info': 'fa-info-circle'
            }[type] || 'fa-info-circle';
            
            const alertHtml = `
                <div class="alert ${alertClass} alert-dismissible fade show position-fixed" 
                     style="top: 20px; right: 20px; z-index: 9999; min-width: 300px;" role="alert">
                    <i class="fas ${iconClass}"></i> ${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            `;
            
            $('body').append(alertHtml);
            
            // Auto-hide after duration
            setTimeout(() => {
                $('.alert').fadeOut('slow', function() {
                    $(this).remove();
                });
            }, duration);
        },
        
        // Confirm dialog
        confirm: function(message, callback) {
            if (confirm(message)) {
                if (typeof callback === 'function') {
                    callback();
                }
                return true;
            }
            return false;
        },
        
        // Generate random ID
        generateId: function() {
            return 'id_' + Math.random().toString(36).substr(2, 9);
        }
    },
    
    // API functions
    api: {
        // Generic GET request
        get: function(endpoint, params, callback) {
            const url = STMS.config.apiBaseUrl + endpoint;
            const queryString = params ? '?' + new URLSearchParams(params).toString() : '';
            
            fetch(url + queryString, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                }
            })
            .then(response => response.json())
            .then(data => {
                if (typeof callback === 'function') {
                    callback(null, data);
                }
            })
            .catch(error => {
                console.error('API Error:', error);
                if (typeof callback === 'function') {
                    callback(error, null);
                }
            });
        },
        
        // Generic POST request
        post: function(endpoint, data, callback) {
            const url = STMS.config.apiBaseUrl + endpoint;
            
            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(data)
            })
            .then(response => response.json())
            .then(data => {
                if (typeof callback === 'function') {
                    callback(null, data);
                }
            })
            .catch(error => {
                console.error('API Error:', error);
                if (typeof callback === 'function') {
                    callback(error, null);
                }
            });
        },
        
        // Generic PUT request
        put: function(endpoint, data, callback) {
            const url = STMS.config.apiBaseUrl + endpoint;
            
            fetch(url, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(data)
            })
            .then(response => response.json())
            .then(data => {
                if (typeof callback === 'function') {
                    callback(null, data);
                }
            })
            .catch(error => {
                console.error('API Error:', error);
                if (typeof callback === 'function') {
                    callback(error, null);
                }
            });
        },
        
        // Generic DELETE request
        delete: function(endpoint, callback) {
            const url = STMS.config.apiBaseUrl + endpoint;
            
            fetch(url, {
                method: 'DELETE',
                headers: {
                    'Accept': 'application/json'
                }
            })
            .then(response => {
                if (response.ok) {
                    if (typeof callback === 'function') {
                        callback(null, { success: true });
                    }
                } else {
                    throw new Error('Delete failed');
                }
            })
            .catch(error => {
                console.error('API Error:', error);
                if (typeof callback === 'function') {
                    callback(error, null);
                }
            });
        }
    },
    
    // Table functions
    table: {
        // Initialize DataTable
        init: function(tableId, options) {
            const defaultOptions = {
                responsive: true,
                pageLength: STMS.config.pagination.defaultSize,
                lengthMenu: [[10, 20, 50, 100], [10, 20, 50, 100]],
                language: {
                    search: "Search:",
                    lengthMenu: "Show _MENU_ entries",
                    info: "Showing _START_ to _END_ of _TOTAL_ entries",
                    paginate: {
                        first: "First",
                        last: "Last",
                        next: "Next",
                        previous: "Previous"
                    }
                },
                dom: '<"row"<"col-sm-12 col-md-6"l><"col-sm-12 col-md-6"f>>' +
                     '<"row"<"col-sm-12"tr>>' +
                     '<"row"<"col-sm-12 col-md-5"i><"col-sm-12 col-md-7"p>>'
            };
            
            const finalOptions = Object.assign(defaultOptions, options || {});
            
            if ($.fn.DataTable) {
                return $('#' + tableId).DataTable(finalOptions);
            }
        },
        
        // Refresh table data
        refresh: function(table) {
            if (table && typeof table.ajax === 'object') {
                table.ajax.reload();
            }
        }
    },
    
    // Form functions
    form: {
        // Serialize form to object
        serialize: function(formId) {
            const form = document.getElementById(formId);
            if (!form) return {};
            
            const formData = new FormData(form);
            const data = {};
            
            for (let [key, value] of formData.entries()) {
                data[key] = value;
            }
            
            return data;
        },
        
        // Reset form
        reset: function(formId) {
            const form = document.getElementById(formId);
            if (form) {
                form.reset();
                // Remove validation classes
                form.querySelectorAll('.is-invalid').forEach(field => {
                    field.classList.remove('is-invalid');
                });
            }
        },
        
        // Populate form with data
        populate: function(formId, data) {
            const form = document.getElementById(formId);
            if (!form || !data) return;
            
            Object.keys(data).forEach(key => {
                const field = form.querySelector(`[name="${key}"]`);
                if (field) {
                    if (field.type === 'checkbox') {
                        field.checked = data[key];
                    } else if (field.type === 'radio') {
                        if (field.value === data[key]) {
                            field.checked = true;
                        }
                    } else {
                        field.value = data[key] || '';
                    }
                }
            });
        }
    },
    
    // Chart functions (for future use with Chart.js)
    chart: {
        // Create line chart
        line: function(canvasId, data, options) {
            // Implementation for Chart.js line chart
            console.log('Line chart:', canvasId, data, options);
        },
        
        // Create bar chart
        bar: function(canvasId, data, options) {
            // Implementation for Chart.js bar chart
            console.log('Bar chart:', canvasId, data, options);
        },
        
        // Create pie chart
        pie: function(canvasId, data, options) {
            // Implementation for Chart.js pie chart
            console.log('Pie chart:', canvasId, data, options);
        }
    }
};

// Document ready functions
$(document).ready(function() {
    // Initialize tooltips
    $('[data-bs-toggle="tooltip"]').tooltip();
    
    // Initialize popovers
    $('[data-bs-toggle="popover"]').popover();
    
    // Auto-hide alerts
    setTimeout(function() {
        $('.alert').fadeOut('slow');
    }, 5000);
    
    // Form validation on submit
    $('form[data-validate="true"]').on('submit', function(e) {
        const formId = $(this).attr('id');
        if (formId && !STMS.utils.validateForm(formId)) {
            e.preventDefault();
            STMS.utils.showNotification('Please fill in all required fields', 'warning');
        }
    });
    
    // Confirm delete buttons
    $('[data-confirm-delete]').on('click', function(e) {
        const message = $(this).data('confirm-delete') || 'Are you sure you want to delete this item?';
        if (!confirm(message)) {
            e.preventDefault();
        }
    });
    
    // Auto-format currency inputs
    $('input[data-currency="true"]').on('blur', function() {
        const value = parseFloat($(this).val());
        if (!isNaN(value)) {
            $(this).val(value.toFixed(2));
        }
    });
    
    // Auto-format date inputs
    $('input[type="date"]').on('change', function() {
        const date = new Date($(this).val());
        if (date instanceof Date && !isNaN(date)) {
            // Date is valid
            $(this).removeClass('is-invalid');
        } else {
            $(this).addClass('is-invalid');
        }
    });
    
    // Mobile sidebar toggle
    $('.navbar-toggler').on('click', function() {
        $('.sidebar').toggleClass('show');
    });
    
    // Close sidebar on mobile when clicking outside
    $(document).on('click', function(e) {
        if ($(window).width() <= 768) {
            if (!$(e.target).closest('.sidebar, .navbar-toggler').length) {
                $('.sidebar').removeClass('show');
            }
        }
    });
    
    // Add loading state to buttons with data-loading attribute
    $('[data-loading]').on('click', function() {
        const $btn = $(this);
        const originalText = $btn.html();
        const loadingText = $btn.data('loading') || 'Loading...';
        
        $btn.html('<i class="fas fa-spinner fa-spin"></i> ' + loadingText);
        $btn.prop('disabled', true);
        
        // Re-enable after 5 seconds (fallback)
        setTimeout(function() {
            $btn.html(originalText);
            $btn.prop('disabled', false);
        }, 5000);
    });
    
    // Initialize any DataTables
    if ($.fn.DataTable) {
        $('.data-table').each(function() {
            const tableId = $(this).attr('id');
            if (tableId) {
                STMS.table.init(tableId);
            }
        });
    }
    
    // Print functionality
    $('[data-print]').on('click', function() {
        const target = $(this).data('print');
        if (target) {
            const printContent = $(target).html();
            const printWindow = window.open('', '_blank');
            printWindow.document.write(`
                <html>
                <head>
                    <title>Print - STMS</title>
                    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
                    <style>
                        body { font-family: Arial, sans-serif; }
                        .no-print { display: none !important; }
                        @media print {
                            .btn, .alert { display: none !important; }
                        }
                    </style>
                </head>
                <body>
                    <div class="container-fluid">
                        ${printContent}
                    </div>
                    <script>
                        window.onload = function() {
                            window.print();
                            window.close();
                        }
                    </script>
                </body>
                </html>
            `);
            printWindow.document.close();
        }
    });
    
    // Export functionality (placeholder)
    $('[data-export]').on('click', function() {
        const format = $(this).data('export');
        const table = $(this).data('table');
        
        STMS.utils.showNotification(`Exporting to ${format.toUpperCase()}...`, 'info');
        
        // Implementation for actual export would go here
        setTimeout(() => {
            STMS.utils.showNotification(`Export to ${format.toUpperCase()} completed!`, 'success');
        }, 2000);
    });
});

// Global error handler
window.addEventListener('error', function(e) {
    console.error('Global error:', e.error);
    // Could send error to logging service here
});

// Expose STMS globally
window.STMS = STMS;

