> ssh is very unsecure protocol and your network is now not behind the firewall. Please follow the steps to implement first.
>
>  
>
> \1. vim /etc/ssh/sshd_config
>
> \2. remove # and change the default port to 72.
>
> \3. PermitRootLogin no
>
> \4. /etc/init.d/ssh restart