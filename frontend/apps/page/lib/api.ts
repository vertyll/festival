import { accountApi } from "@festival/shared/api/account";
import { browserHttp } from "@festival/shared/api/http";
import { publicApi } from "@festival/shared/api/public";

export const api = publicApi(browserHttp);
export const account = accountApi(browserHttp);
