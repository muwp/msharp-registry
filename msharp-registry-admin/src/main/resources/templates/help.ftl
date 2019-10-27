<!DOCTYPE html>
<html>
<head>
    <#import "./common/common.macro.ftl" as netCommon>
        <title>分布式服务注册中心</title>
    <@netCommon.commonStyle />
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap? exists && cookieMap["registry_adminlte_settings"]?exists && "off" == cookieMap["registry_adminlte_settings"].value >sidebar-collapse</#if> ">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
        <!-- left -->
    <@netCommon.commonLeft "help" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>使用教程
                <small></small>
            </h1>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="callout callout-info">
                <h4>分布式服务注册中心MSharp-REGISTRY</h4>
                <br>
                <p>
                    <#--<a target="_blank" href="https://github.com/muwp/msharp-registry">Github</a>&nbsp;&nbsp;&nbsp;&nbsp;-->
                    <#--<br><br>-->
                    <#--<br><br>-->
                </p>
                <p></p>
            </div>
        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->

    <!-- footer -->
    <@netCommon.commonFooter />
</div>
<@netCommon.commonScript />
</body>
</html>
