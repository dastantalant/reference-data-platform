import { decodeErrors } from '@/global';
import { useHttp } from '@/http';

export function useAuth() {
  const http = useHttp();

  const login = async (username: string, password: string) => {
    try {
      const response = await http.post('/login', {
        username,
        password,
      });
      return response.data;
    } catch (e) {
      throw e.response.data;
    }
  };

  const register = async (data) => {
    try {
      const response = await http.post('/register/create', {
        ...data,
        name: `${data.first_name} ${data.last_name}`,
      });
      return response.data;
    } catch (e) {
      throw decodeErrors(e.response.data.errors);
    }
  };

  const verifyEmail = async (code: string) => {
    try {
      const response = await http.post('/register/verify', { code });
      return response.data.username;
    } catch (e) {
      return e.response.data;
    }
  };

  const resendVerifyEmail = async (code: string) => {
    try {
      await http.post('/register/resend', {
        code,
      });
    } catch (e) {
      return e.response.data;
    }
  };

  const sendResetLink = async (username: string) => {
    try {
      await http.post('/reset-link', { username });
    } catch (e) {
      return e.response.data.error;
    }
  };

  const resetPassword = async (data) => {
    try {
      const response = await http.post('/reset-password', data);
      return response.data;
    } catch (e) {
      return e.response.data;
    }
  };

  const validatePassword = async (data) => {
    try {
      await http.post('/profile/validate-password', data);
    } catch (e) {
      return e.response.data.message;
    }
  };

  const updatePassword = async (data) => {
    try {
      await http.post('/profile/update-password', data);
    } catch (e) {
      return e.response.data;
    }
  };

  const loadPermissions = async () => {
    const response = await http.get('/profile/permissions');
    return response.data;
  };

  const loadLicense = async () => {
    const response = await http.get('/profile/license');
    return response.data;
  };

  return {
    login,
    register,
    verifyEmail,
    resendVerifyEmail,
    sendResetLink,
    resetPassword,
    validatePassword,
    updatePassword,
    loadPermissions,
    loadLicense,
  };
}
