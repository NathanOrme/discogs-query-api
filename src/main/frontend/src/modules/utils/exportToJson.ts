const exportToJson = (data: unknown, filename: string): void => {
  const jsonString = `data:text/json;charset=utf-8,${encodeURIComponent(
    JSON.stringify(data, null, 2)
  )}`;
  const link = document.createElement('a');
  link.href = jsonString;
  link.download = String(filename) + '.json';
  link.click();
};

export default exportToJson;
