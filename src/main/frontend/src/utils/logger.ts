export type LogLevel = 'debug' | 'info' | 'warn' | 'error';

export interface LogEntry {
  level: LogLevel;
  message: unknown[];
  timestamp: string;
}

interface AppLogger {
  buffer: LogEntry[];
  maxBuffer: number;
  push: (level: LogLevel, ...args: unknown[]) => void;
  flush: () => LogEntry[];
}

const createLogger = (): AppLogger => {
  const buffer: LogEntry[] = [];
  const maxBuffer = 1000;

  const push = (level: LogLevel, ...args: unknown[]) => {
    if (buffer.length >= maxBuffer) buffer.shift();
    buffer.push({ level, message: args, timestamp: new Date().toISOString() });
  };

  const flush = () => buffer.slice();

  return { buffer, maxBuffer, push, flush };
};

export const appLogger: AppLogger = createLogger();

export const logger = {
  debug: (...args: unknown[]) => console.debug(...args),
  info: (...args: unknown[]) => console.info(...args),
  warn: (...args: unknown[]) => console.warn(...args),
  error: (...args: unknown[]) => console.error(...args),
  flush: () => appLogger.flush(),
};
