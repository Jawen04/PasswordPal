export function generatePassword(length, options = {}) {
  if (length <= 0) return '';

  const {
    includeUppercase = true,
    includeLowercase = true,
    includeNumbers = true,
    includeSymbols = true,
  } = options;

  const lowercase = 'abcdefghijklmnopqrstuvwxyz';
  const uppercase = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
  const numbers = '0123456789';
  const symbols = `!\"#$%&'()*+,-./:;<=>?@[\\]^_\`{|}~`;

  let charPool = '';
  if (includeLowercase) charPool += lowercase;
  if (includeUppercase) charPool += uppercase;
  if (includeNumbers) charPool += numbers;
  if (includeSymbols) charPool += symbols;

  if (charPool.length === 0) {
    throw new Error('At least one character type must be selected.');
  }

  let password = '';
  const array = new Uint32Array(length);
  window.crypto.getRandomValues(array);

  for (let i = 0; i < length; i++) {
    const idx = array[i] % charPool.length;
    password += charPool.charAt(idx);
  }

  return password;
}
