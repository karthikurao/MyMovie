jest.setTimeout(30000);

import axios from 'axios';
import '../api/axiosConfig';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080';
const ADMIN_CREDENTIALS = {
    email: 'admin@mymovie.com',
    password: 'admin123',
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
    beforeAll(() => {
        axios.defaults.baseURL = API_BASE_URL;
    });

    beforeEach(() => {
        localStorage.clear();
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
