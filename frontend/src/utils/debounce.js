/**
 * 防抖函数
 * @param {Function} func 目标函数
 * @param {Number} wait 延迟执行毫秒数
 */
export function debounce(func, wait) {
  let timeout;
  return function () {
    const context = this;
    const args = arguments;
    if (timeout) clearTimeout(timeout);
    timeout = setTimeout(() => {
      func.apply(context, args);
    }, wait);
  };
}
