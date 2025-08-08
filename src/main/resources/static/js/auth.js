const API_BASE = 'http://localhost:8080/api';

function showAlert(message, type = 'error') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type}`;
    alertDiv.textContent = message;
    
    const form = document.querySelector('.auth-form');
    form.insertBefore(alertDiv, form.firstChild);
    
    setTimeout(() => {
        alertDiv.remove();
    }, 5000);
}

function showLoading(button) {
    const originalText = button.textContent;
    button.innerHTML = '<span class="loading"></span> Loading...';
    button.disabled = true;
    return originalText;
}

function hideLoading(button, originalText) {
    button.textContent = originalText;
    button.disabled = false;
}

// Login form
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const resetForm = document.getElementById('resetForm');
    
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
    
    if (registerForm) {
        registerForm.addEventListener('submit', handleRegister);
    }
    
    if (resetForm) {
        resetForm.addEventListener('submit', handleResetPassword);
    }
});

async function handleLogin(event) {
    event.preventDefault();
    
    const submitButton = event.target.querySelector('button[type="submit"]');
    const originalText = showLoading(submitButton);
    
    const formData = new FormData(event.target);
    const loginData = {
        email: formData.get('email'),
        password: formData.get('password')
    };
    
    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(loginData)
        });
        
        const result = await response.json();
        
        if (response.ok) {
            // Store token and redirect
            localStorage.setItem('authToken', result.token);
            localStorage.setItem('userRole', result.role);
            
            showAlert('Login successful!', 'success');
            
            // Redirect based on role
            setTimeout(() => {
                if (result.role === 'ADMIN') {
                    window.location.href = '/admindashboard';
                } else {
                    window.location.href = '/dashboard';
                }
            }, 1000);
        } else {
            showAlert(result.message || 'Login failed');
        }
    } catch (error) {
        showAlert('Network error. Please try again.');
    } finally {
        hideLoading(submitButton, originalText);
    }
}

async function handleRegister(event) {
    event.preventDefault();
    
    const submitButton = event.target.querySelector('button[type="submit"]');
    const originalText = showLoading(submitButton);
    
    const formData = new FormData(event.target);
    const registerData = {
        email: formData.get('email'),
        password: formData.get('password'),
        phoneNumber: formData.get('phoneNumber')
    };
    
    try {
        const response = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(registerData)
        });
        
        const result = await response.json();
        
        if (response.ok) {
            localStorage.setItem('authToken', result.token);
            localStorage.setItem('userRole', result.role);
            
            showAlert('Registration successful!', 'success');
            
            setTimeout(() => {
                window.location.href = '/dashboard';
            }, 1000);
        } else {
            showAlert(result.message || 'Registration failed');
        }
    } catch (error) {
        showAlert('Network error. Please try again.');
    } finally {
        hideLoading(submitButton, originalText);
    }
}

async function handleResetPassword(event) {
    event.preventDefault();
    
    const submitButton = event.target.querySelector('button[type="submit"]');
    const originalText = showLoading(submitButton);
    
    const formData = new FormData(event.target);
    const email = formData.get('email');
    
    try {
        // Not finished
        showAlert('Reset instructions sent to your email!', 'success');
        
        setTimeout(() => {
            window.location.href = '/login';
        }, 2000);
    } catch (error) {
        showAlert('Network error. Please try again.');
    } finally {
        hideLoading(submitButton, originalText);
    }
}

// Token validation
function checkAuth() {
    const token = localStorage.getItem('authToken');
    if (token) {
        fetch(`${API_BASE}/auth/validate?token=${token}`)
            .then(response => {
                if (!response.ok) {
                    logout();
                }
            })
            .catch(() => logout());
    }
}

function logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userRole');
    window.location.href = '/login';
}

checkAuth();