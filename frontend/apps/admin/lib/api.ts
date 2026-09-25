import { adminApi } from "@festival/shared/api/admin";
import { browserHttp } from "@festival/shared/api/http";

export const admin = adminApi(browserHttp);
