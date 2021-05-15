function getCurrentPath() {
    let path = window.location.pathname;
    return path.endsWith('/') ? path : path + '/';
}

export function fetch(path, init) {
    if (path.startsWith("$currentPath/")) {
        path = getCurrentPath() + path.substr("$currentPath/".length);
    }
    return window.fetch(path, init)
        .then(response => {
            if (response.ok) {
                return response;
            } else {
                return response.text().then((responseBody) => {
                    const error = new Error(`Unexpected ${response.status} (${response.statusText}) response`);
                    error.responseBody = responseBody; // Could make a custom error class, but this'll do
                    throw error;
                });
            }
        });
}

export function parseJsonResponse(response) {
    const isEmpty = response.headers.get("Content-Length") === "0";
    return isEmpty ? undefined : response.json();
}

export function getJson(path, init) {
    return fetch(path, init).then(parseJsonResponse);
}

export function post(path, init) {
    init = init || {};
    init.headers = init.headers || {};
    return fetch(path, {method: 'POST', ...init});
}

export function postJson(path, init) {
    init = init || {};
    init.headers = init.headers || {};
    if (init.body) {
        init.body = JSON.stringify(init.body);
    }
    return post(path, {...init, headers: {'Content-Type': 'application/json', ...init.headers}});
}

function postWithActionForReload(postAction, path, init, newHash) {
    postAction(path, init)
        .then(() => {
            if (newHash) {
                window.location.hash = newHash;
            }
            window.location.reload();
        })
        .catch(logAndAlertError);
}

export function postForReload(path, init, newHash) {
    postWithActionForReload(post, path, init, newHash);
}

export function postJsonForReload(path, init, newHash) {
    postWithActionForReload(postJson, path, init, newHash);
}

export function postJsonForCreate(path, init) {
  postJson(path, init)
    .then(response => window.location = response.headers.get("Location"))
    .catch(logAndAlertError);
}

export function postJsonAndParseResponse(path, init) {
    return postJson(path, init).then(parseJsonResponse);
}

function renderAsStringIfPresent(prefix, object) {
    if (!object) {
        return '';
    }
    return prefix + object;
}

function renderErrorsAsStringIfPresent(prefix, object) {
    if (!object) {
        return '';
    }
    return prefix + Object.values(object).flatMap(l => l).join(", ");
}

export function logAndAlertError(error) {
    if (error.responseBody) {
        logAndAlertError(JSON.parse(error.responseBody));
        return;
    }
    console.log(JSON.stringify(error));
    const messageToDisplay = renderAsStringIfPresent('', error.title)
            + renderAsStringIfPresent(' - ', error.detail)
            + renderErrorsAsStringIfPresent(' - ', error.errors);
    alert(messageToDisplay);
}
