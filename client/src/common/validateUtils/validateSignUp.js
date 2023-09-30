export const isEmail = (email) => /\S+@\S+\.\S+/.test(email);

export const isURL = (url) =>
  /[-a-zA-Z0-9@:%._\\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)/.test(url);

export const arePasswordsSame = (password, confirmedPassword) => password === confirmedPassword;

export const isPasswordStrong = (password) =>
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%\\^&\\*])(?=.{8,})/.test(password);

export const markValidation = (condition, setValidate) => (!condition ? setValidate(false) : setValidate(true));

export const markValidationPassword = (passwordInput, confirmedPasswordInput, setValidate) => {
  const condition = arePasswordsSame(passwordInput, confirmedPasswordInput);
  markValidation(condition, setValidate);
};
