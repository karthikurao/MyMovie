import { ensureSeconds, isEndAfterStart } from '../utils/datetime';

describe('datetime utils', () => {
    test('ensureSeconds appends :00 when missing', () => {
        expect(ensureSeconds('2025-11-03T14:30')).toBe('2025-11-03T14:30:00');
    });

    test('ensureSeconds leaves seconds intact', () => {
        expect(ensureSeconds('2025-11-03T14:30:45')).toBe('2025-11-03T14:30:45');
    });

    test('isEndAfterStart validates ordering', () => {
        expect(isEndAfterStart('2025-11-03T14:30', '2025-11-03T15:00')).toBe(true);
        expect(isEndAfterStart('2025-11-03T15:00', '2025-11-03T15:00')).toBe(false);
        expect(isEndAfterStart('2025-11-03T16:00', '2025-11-03T15:00')).toBe(false);
    });
});
