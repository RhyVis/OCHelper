package com.rhynia.ochelper.servlet;

import com.rhynia.ochelper.dao.AEFluidDao;
import com.rhynia.ochelper.dao.AEItemDao;
import com.rhynia.ochelper.util.Format;
import com.rhynia.ochelper.var.AEFluid;
import com.rhynia.ochelper.var.AEItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

import static com.rhynia.ochelper.util.LocalizationMap.NAME_MAP_FLUID;
import static com.rhynia.ochelper.util.LocalizationMap.NAME_MAP_ITEM;

@Component
@WebServlet(name = "ae-storage-report", value = "/ae-storage-report")
public class AEStorageReport extends HttpServlet {

    private final AEItemDao aei;
    private final AEFluidDao aef;

    AEStorageReport(AEItemDao aei, AEFluidDao aef) {
        this.aei = aei;
        this.aef = aef;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<link rel=\"stylesheet\" href=\"dist/css/adminlte.min.css\">");
        out.println("<script src=\"dist/js/adminlte.min.js\"></script>");
        out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, minimum-scale=0.5, maximum-scale=2.0, user-scalable=yes\" /> ");

        out.println("<html><head><title>AE Storage Center</title><link rel=\"icon\" href=\"res/icon.png\" sizes=\"16x16\"></head>");
        out.println("<body>");

        out.println("<script src=\"https://cdn.jsdelivr.net/npm/overlayscrollbars@2.3.0/browser/overlayscrollbars.browser.es6.min.js\" integrity=\"sha256-H2VM7BKda+v2Z4+DRy69uknwxjyDRhszjXFhsL4gD3w=\" crossorigin=\"anonymous\"></script>");
        out.println("<script src=\"https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js\" integrity=\"sha256-whL0tQWoY1Ku1iskqPFvmZ+CHsvmRWx/PIoEvIeWh4I=\" crossorigin=\"anonymous\"></script>");
        out.println("<script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.min.js\" integrity=\"sha256-YMa+wAM6QkVyz999odX7lPRxkoYAan8suedu4k2Zur8=\" crossorigin=\"anonymous\"></script>");

        out.println("<main class=\"app-main\">");
        out.println("</br>");
        out.println("<h1 style=\"text-align: center;\"><span class=\"info-box-icon text-bg-secondary shadow-sm\" style=\"min-width: 1000px;min-height: 500px;\"><   物品   ></span></h1>");
        out.println("</br>");
        out.println("<div class=\"container-fluid\">");
        out.println("<div class=\"row\">");

        String iconPath = "dist/assets/img/item_table/";
        String altPath = "res/icon.png";

        List<AEItem> items;
        List<AEFluid> fluids;
        try {
            items = aei.getAEItemList();
            fluids = aef.getAEFluidList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (AEItem item : items) {
            if (Objects.equals(item.getName(), "ae2fc:fluid_drop")) continue;
            if (Objects.equals(item.getSize(), "0")) continue;

            Pair<String, Integer> sign = Pair.of(item.getName(), item.getDamage());
            String local;
            if (NAME_MAP_ITEM.containsKey(sign)) {
                local = NAME_MAP_ITEM.get(sign);
            } else {
                local = item.getLabel();
            }

            String imgLocal = "<img src=\"" + iconPath + local + ".png\", alt=\"" + altPath + "\", width=\"50\", height=\"50\">";

            out.println("<div class=\"col-12 col-sm-6 col-md-3\" style=\"min-width: 100px;\">");
            out.println("<div class=\"info-box\"> <span class=\"info-box-icon text-bg-light shadow-sm\" style=\"min-width: 80px;min-height: 80px;\">" + imgLocal + "</span>");
            out.println("<div class=\"info-box-content\"> <span class=\"info-box-text\">"
                    + (item.isHasTag() ? item.getLabel() : local)
                    + "</span> <span class=\"info-box-number\" style=\"min-width: 180px;\">"
                    + Format.formatStringSizeDisplay(item.getSize()) + " "
                    + Format.formatStringSizeByte(item.getSize()) + "</span> </div> </div>");
            out.println("</div>");
        }
        out.println("</div></div>");

        out.println("</br>");
        out.println("<h1 style=\"text-align: center;\"><span class=\"info-box-icon text-bg-secondary shadow-sm\" style=\"min-width: 1000px;min-height: 500px;\"><   流体   ></span></h1>");
        out.println("</br>");
        out.println("<div class=\"container-fluid\">");
        out.println("<div class=\"row\">");
        for (AEFluid fluid : fluids) {
            String local;
            if (NAME_MAP_FLUID.containsKey(fluid.getName())) {
                local = NAME_MAP_FLUID.get(fluid.getName());
            } else {
                local = fluid.getLabel();
            }

            String imgLocal = "<img src=\"" + iconPath + Format.trySwitchFluidName(local) + "单元.png\", alt=\"" + altPath + "\", width=\"50\", height=\"50\">";

            out.println("<div class=\"col-12 col-sm-6 col-md-3\" style=\"min-width: 100px;\">");
            out.println("<div class=\"info-box\"> <span class=\"info-box-icon text-bg-light shadow-sm\" style=\"min-width: 80px;min-height: 80px;\">" + imgLocal + "</span>");
            out.println("<div class=\"info-box-content\"> <span class=\"info-box-text\">"
                    + local + "</span> <span class=\"info-box-number\" style=\"min-width: 180px;\">"
                    + Format.formatStringSizeDisplay(fluid.getAmount()) + " "
                    + Format.formatStringSizeByte(fluid.getAmount()) + "</span> </div> </div>");
            out.println("</div>");
        }
        out.println("</div></div>");

        out.println("</main>");
        out.println("</body></html>");
    }
}
