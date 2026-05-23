const rawConfig = typeof window !== 'undefined' ? (window.__APP_CONFIG__ || {}) : {};

const normalizeBaseUrl = (value) => {
  if (!value) {
    return '';
  }
  return String(value).trim().replace(/\/$/, '');
};

const apiBaseUrl = normalizeBaseUrl(
  rawConfig.apiBaseUrl || rawConfig.API_BASE_URL || 'http://127.0.0.1:8080'
);

const buildApiUrl = (path = '') => {
  if (!path) {
    return apiBaseUrl;
  }
  if (/^https?:\/\//i.test(path)) {
    return path;
  }
  if (!apiBaseUrl) {
    return path;
  }
  return `${apiBaseUrl}${path.startsWith('/') ? '' : '/'}${path}`;
};

export { apiBaseUrl, buildApiUrl };
