import axios from 'axios';

const AUTH_HEADER_KEY = 'Authorization';
const DEFAULT_TOKEN_TYPE = 'Bearer';
const REFRESH_ENDPOINT = '/api/users/refresh';

let refreshClient = axios.create();
let lastKnownBaseUrl = null;

const applyBaseUrl = (baseUrl) => {
    if (!baseUrl) {
        return;
    }

    lastKnownBaseUrl = baseUrl;

    if (axios.defaults.baseURL !== baseUrl) {
        axios.defaults.baseURL = baseUrl;
    }

    if (refreshClient?.defaults) {
        refreshClient.defaults.baseURL = baseUrl;
    }
};

const configuredBaseUrl = process.env.REACT_APP_API_BASE_URL;
applyBaseUrl(configuredBaseUrl);

let isRefreshing = false;
let failedQueue = [];

const getStoredUser = () => {
    const storedUserRaw = localStorage.getItem('user');
    if (!storedUserRaw) {
        return null;
    }

    try {
        return JSON.parse(storedUserRaw);
    } catch (error) {
        console.warn('Unable to parse stored user for auth token', error);
        return null;
    }
};

const persistUser = (user) => {
    localStorage.setItem('user', JSON.stringify(user));
    window.dispatchEvent(new CustomEvent('userUpdate'));
};

const dispatchSessionExpiry = (message) => {
    const detail = message || 'Your session has expired. Please sign in again.';
    window.dispatchEvent(new CustomEvent('sessionExpired', { detail }));
};

const clearAuthState = (message) => {
    const hadUser = !!localStorage.getItem('user');
    if (hadUser) {
        localStorage.removeItem('user');
        window.dispatchEvent(new CustomEvent('userUpdate'));
        dispatchSessionExpiry(message);
    }
};

const processQueue = (error, userData) => {
    failedQueue.forEach(({ resolve, reject }) => {
        if (error) {
            reject(error);
        } else {
            resolve(userData);
        }
    });
    failedQueue = [];
};

const requestRefreshToken = async (currentUser) => {
    if (!currentUser || !currentUser.refreshToken) {
        throw new Error('Missing refresh token');
    }

    if (currentUser.refreshTokenExpiresAt && Date.now() > currentUser.refreshTokenExpiresAt) {
        throw new Error('Refresh token expired');
    }

    // Ensure the refresh client targets the same API origin as the main axios instance when configured at runtime (e.g. tests)
    const activeBaseUrl = axios.defaults.baseURL || lastKnownBaseUrl;
    if (activeBaseUrl) {
        applyBaseUrl(activeBaseUrl);
    }

    const response = await refreshClient.post(
        REFRESH_ENDPOINT,
        { refreshToken: currentUser.refreshToken },
        { headers: { 'Content-Type': 'application/json' } }
    );

    const data = response?.data;

    if (data?.token && data?.refreshToken) {
        const now = Date.now();
        const updatedUser = {
            ...currentUser,
            token: data.token,
            tokenType: data.tokenType || DEFAULT_TOKEN_TYPE,
            expiresIn: data.expiresIn,
            expiresAt: typeof data.expiresAt === 'number'
                ? data.expiresAt
                : (typeof data.expiresIn === 'number' ? now + data.expiresIn : null),
            refreshToken: data.refreshToken,
            refreshTokenExpiresIn: data.refreshTokenExpiresIn,
            refreshTokenExpiresAt: typeof data.refreshTokenExpiresAt === 'number'
                ? data.refreshTokenExpiresAt
                : (typeof data.refreshTokenExpiresIn === 'number' ? now + data.refreshTokenExpiresIn : null),
            role: data.role || currentUser.role,
            email: data.email || currentUser.email,
        };

        persistUser(updatedUser);
        return updatedUser;
    }

    throw new Error('Invalid refresh token response');
};

axios.interceptors.request.use(
    (config) => {
        if (!config) {
            return config;
        }

        if (config.url && config.url.includes(REFRESH_ENDPOINT)) {
            return config;
        }

        const storedUser = getStoredUser();
        if (storedUser?.token) {
            const tokenType = storedUser.tokenType || DEFAULT_TOKEN_TYPE;
            config.headers = config.headers || {};
            config.headers[AUTH_HEADER_KEY] = `${tokenType} ${storedUser.token}`;
        }

        return config;
    },
    (error) => Promise.reject(error)
);

axios.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error?.config;

        if (error?.response?.status !== 401 || !originalRequest) {
            return Promise.reject(error);
        }

        if (originalRequest.url && originalRequest.url.includes(REFRESH_ENDPOINT)) {
            clearAuthState(error?.response?.data?.error);
            return Promise.reject(error);
        }

        const storedUser = getStoredUser();
        if (!storedUser?.refreshToken) {
            clearAuthState(error?.response?.data?.error);
            return Promise.reject(error);
        }

        if (storedUser.refreshTokenExpiresAt && Date.now() > storedUser.refreshTokenExpiresAt) {
            clearAuthState('Your refresh session has expired. Please sign in again.');
            return Promise.reject(error);
        }

        if (originalRequest._retry) {
            clearAuthState(error?.response?.data?.error);
            return Promise.reject(error);
        }

        originalRequest._retry = true;

        if (isRefreshing) {
            try {
                const queuedResult = await new Promise((resolve, reject) => {
                    failedQueue.push({ resolve, reject });
                });

                if (queuedResult?.token) {
                    originalRequest.headers = originalRequest.headers || {};
                    originalRequest.headers[AUTH_HEADER_KEY] = `${queuedResult.tokenType || DEFAULT_TOKEN_TYPE} ${queuedResult.token}`;
                }

                return axios(originalRequest);
            } catch (queueError) {
                return Promise.reject(queueError);
            }
        }

        isRefreshing = true;

        try {
            const updatedUser = await requestRefreshToken(storedUser);
            processQueue(null, updatedUser);
            originalRequest.headers = originalRequest.headers || {};
            originalRequest.headers[AUTH_HEADER_KEY] = `${updatedUser.tokenType || DEFAULT_TOKEN_TYPE} ${updatedUser.token}`;
            return axios(originalRequest);
        } catch (refreshError) {
            processQueue(refreshError, null);
            clearAuthState(refreshError?.message);
            return Promise.reject(refreshError);
        } finally {
            isRefreshing = false;
        }
    }
);

export default axios;

export const __setRefreshClient = (client) => {
    refreshClient = client || axios.create();
    if (lastKnownBaseUrl) {
        applyBaseUrl(lastKnownBaseUrl);
    }
};
