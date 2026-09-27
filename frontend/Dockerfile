ARG NODE_IMAGE=node:24-alpine

FROM ${NODE_IMAGE} AS base
RUN corepack enable
WORKDIR /workspace

FROM base AS prune
ARG APP
RUN test -n "$APP" || (echo "Pass --build-arg APP=page or APP=admin" >&2 && exit 1)
COPY . .
RUN pnpm dlx turbo@2.11.4 prune @festival/${APP} --docker

FROM base AS build
ARG APP
ARG BACKEND_INTERNAL_URL
ENV NEXT_TELEMETRY_DISABLED=1 \
    BACKEND_INTERNAL_URL=${BACKEND_INTERNAL_URL}
COPY --from=prune /workspace/out/json/ ./
RUN --mount=type=cache,target=/root/.local/share/pnpm/store \
    pnpm install --frozen-lockfile
COPY --from=prune /workspace/out/full/ ./
COPY tsconfig.base.json next.config.base.mjs ./
RUN pnpm turbo build --filter=@festival/${APP}

FROM ${NODE_IMAGE} AS runtime
ARG APP
ARG BACKEND_INTERNAL_URL
ENV NODE_ENV=production \
    BACKEND_INTERNAL_URL=${BACKEND_INTERNAL_URL} \
    NEXT_TELEMETRY_DISABLED=1 \
    HOSTNAME=0.0.0.0 \
    PORT=3000
WORKDIR /app
COPY --from=build --chown=node:node /workspace/apps/${APP}/.next/standalone/ ./
COPY --from=build --chown=node:node /workspace/apps/${APP}/.next/static/ apps/${APP}/.next/static/
COPY --from=build --chown=node:node /workspace/apps/${APP}/public/ apps/${APP}/public/
WORKDIR /app/apps/${APP}
USER node
EXPOSE 3000
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
    CMD wget -q -O /dev/null "http://127.0.0.1:${PORT}/api/health" || exit 1
CMD ["node", "server.js"]
