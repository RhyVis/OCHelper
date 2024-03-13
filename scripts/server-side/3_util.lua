function tpsAll()
    local raw = c.tps_card.getAllTickTimes()
    local new = {}
    for k, v in pairs(raw) do
        new[tostring(k)] = v
    end
    return new
end