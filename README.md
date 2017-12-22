# GoCD Auth Plugin - Guest User

Allows users to login as a guest to a [GoCD](https://www.go.cd) server.

> ### **Warning**: [GoCD Auth Plugin](https://github.com/gocd-contrib/gocd_auth_plugin_guest_user) uses authentication enpoint. As of GoCD release `17.5.0`, authentication plugin endpoints have been deprecated, and will be removed in GoCD release `18.1.0`. [GoCD Auth Plugin](https://github.com/gocd-contrib/gocd_auth_plugin_guest_user) will not work for GoCD versions `18.1.0` and above. Users are expected to use [GoCD Guest Login Plugin](https://github.com/gocd-contrib/guest-login-plugin) instead.

# Installation

Install by building it and placing it in the plugins/external directory and restarting the server. If installed
properly, you should be able to see a button during login, which allows you to login as a guest
user.

![Image](https://raw.githubusercontent.com/arvindsv/gocd_auth_plugin_guest_user/master/images/LoginAsGuest.gif)
