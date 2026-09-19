(function(){
const TOKEN_KEY="rationPortalToken";
const API=window.RATION_API_BASE||"https://ration-portal-backend.onrender.com/api";
const token=sessionStorage.getItem(TOKEN_KEY);
const path=window.location.pathname;
const isHome=path.endsWith("/")||path.endsWith("/index.html");
if(!token&&!isHome){
  const loginPath=path.includes("/pages/")?"login.html":"pages/login.html";
  const redirect=encodeURIComponent(path+window.location.search);
  window.location.replace(loginPath+"?redirect="+redirect);
  return;
}
const originalFetch=window.fetch.bind(window);
window.fetch=async function(input,init){
  const url=typeof input==="string"?input:(input&&input.url)||"";
  const isApi=url.startsWith(API)||url.startsWith("/api/");
  const currentToken=sessionStorage.getItem(TOKEN_KEY);
  if(isApi&&currentToken){
    const options=init?{...init}:{ };
    const headers=new Headers(options.headers||{});
    headers.set("Authorization","Bearer "+currentToken);
    options.headers=headers;
    init=options;
  }
  const response=await originalFetch(input,init);
  if(isApi&&response.status===401){
    sessionStorage.removeItem("rationPortalToken");
    sessionStorage.removeItem("rationPortalUser");
    const loginPath=path.includes("/pages/")?"login.html":"pages/login.html";
    window.location.replace(loginPath+"?redirect="+encodeURIComponent(path+window.location.search));
  }
  return response;
};
})();
