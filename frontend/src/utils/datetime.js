export const ensureSeconds = (value) => {
    if (!value) return '';
    return value.length === 16 ? `${value}:00` : value;
};

export const isEndAfterStart = (start, end) => {
    if (!start || !end) return false;
    const s = new Date(ensureSeconds(start));
    const e = new Date(ensureSeconds(end));
    return e > s;
};
