export const PLUGIN_HTTP = Symbol.for('axios');
export const TOAST_LIFE = 4000;

export function decodeErrors(errors: any[]) {
  return errors.reduce((result, { rule, field }) => {
    if (!result[field]) {
      result[field] = [];
    }
    result[field].push(rule);
    return result;
  }, {});
}
