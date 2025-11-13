jest.setTimeout(30000);

import MockAdapter from 'axios-mock-adapter';
import axios, { __setRefreshClient } from '../api/axiosConfig';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080';
const ADMIN_CREDENTIALS = {
    email: 'admin@mymovie.com',
    password: 'admin123',
};

const createLoginPayload = () => {
    const now = Date.now();
    return {
        success: true,
        token: 'initial-access-token',
        refreshToken: 'initial-refresh-token',
        tokenType: 'Bearer',
        email: ADMIN_CREDENTIALS.email,
        role: 'ADMIN',
        userId: 1,
        expiresIn: 3600000,
        expiresAt: now + 3600000,
        refreshTokenExpiresIn: 7200000,
        refreshTokenExpiresAt: now + 7200000,
    };
};

const createRefreshPayload = () => {
    const now = Date.now();
    return {
        token: 'refreshed-access-token',
        refreshToken: 'rotated-refresh-token',
        tokenType: 'Bearer',
        expiresIn: 3600000,
        expiresAt: now + 3600000,
        refreshTokenExpiresIn: 7200000,
        refreshTokenExpiresAt: now + 7200000,
        role: 'ADMIN',
        email: ADMIN_CREDENTIALS.email,
    };
};

const parseStoredUser = () => {
    const stored = localStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
};

const persistAuthPayload = (payload) => {
    const persistedUser = {
        token: payload.token,
        refreshToken: payload.refreshToken,
        tokenType: payload.tokenType || 'Bearer',
        email: payload.email,
        role: payload.role,
        userId: payload.userId,
        expiresIn: payload.expiresIn,
        expiresAt: payload.expiresAt,
        refreshTokenExpiresIn: payload.refreshTokenExpiresIn,
        refreshTokenExpiresAt: payload.refreshTokenExpiresAt,
    };

    localStorage.setItem('user', JSON.stringify(persistedUser));
    window.dispatchEvent(new CustomEvent('userUpdate'));
    return persistedUser;
};

describe('Axios auth wiring (integration)', () => {
    let mock;

    beforeAll(() => {
        __setRefreshClient(axios);
        axios.defaults.baseURL = API_BASE_URL;
        mock = new MockAdapter(axios);
    });

    beforeEach(() => {
        localStorage.clear();
        mock.resetHandlers();
        mock.resetHistory();

        mock.onPost('/api/users/signin').reply(() => [200, createLoginPayload()]);

        mock.onGet('/api/bookings').reply((config) => {
            const authHeader = config.headers?.Authorization || '';
            if (authHeader.includes('invalid-token')) {
                return [401, { error: 'Unauthorized' }];
            }

            if (!authHeader) {
                return [401, { error: 'Missing token' }];
            }

            return [200, [{ id: 'booking-1' }]];
        });

        mock.onPost('/api/users/refresh').reply((config) => {
            const payload = JSON.parse(config.data || '{}');
            if (payload.refreshToken !== 'initial-refresh-token') {
                return [400, { error: 'Invalid refresh token' }];
            }

            return [200, createRefreshPayload()];
        });
    });

    afterAll(() => {
        mock.restore();
    });

    it('completes login and persists auth payload', async () => {
        const signinResponse = await axios.post('/api/users/signin', ADMIN_CREDENTIALS, {
            headers: { 'Content-Type': 'application/json' },
            timeout: 10000,
        });

        expect(signinResponse.status).toBe(200);
        expect(signinResponse.data?.success).toBe(true);

        const storedUser = persistAuthPayload(signinResponse.data);
        expect(storedUser.email).toBe(ADMIN_CREDENTIALS.email);
        expect(storedUser.token).toBeTruthy();
        expect(storedUser.refreshToken).toBeTruthy();

        const bookingsResponse = await axios.get('/api/bookings', { timeout: 10000 });
        expect(bookingsResponse.status).toBe(200);
    });

    it('refreshes the access token transparently when a request is rejected', async () => {
        const signinResponse = await axios.post('/api/users/signin', ADMIN_CREDENTIALS, {
            headers: { 'Content-Type': 'application/json' },
            timeout: 10000,
        });

        expect(signinResponse.status).toBe(200);
        expect(signinResponse.data?.success).toBe(true);

        const storedUser = persistAuthPayload(signinResponse.data);

        const tamperedUser = {
            ...storedUser,
            token: 'invalid-token',
        };
        localStorage.setItem('user', JSON.stringify(tamperedUser));
        window.dispatchEvent(new CustomEvent('userUpdate'));

        const refreshedResponse = await axios.get('/api/bookings', { timeout: 10000 });
        expect(refreshedResponse.status).toBe(200);

        const refreshedUser = parseStoredUser();
        expect(refreshedUser).toBeTruthy();
        expect(refreshedUser.token).toBeTruthy();
        expect(refreshedUser.token).not.toBe('invalid-token');
    });
});
