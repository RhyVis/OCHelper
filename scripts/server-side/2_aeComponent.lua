function aeItem()
    return c.me_interface.getItemsInNetwork()
end

function aeFluid()
    return c.me_interface.getFluidsInNetwork()
end

function aeCpuInfo()
    local cpus = c.me_interface.getCpus()
    for k, v in pairs(cpus) do
        v["cpuid"] = k
        v["cpu"] = nil
    end
    return cpus
end

function aeCpuDetail1(cpuid)
    local cpu = c.me_interface.getCpus()[cpuid]['cpu']
    return cpu.activeItems()
end

function aeCpuDetail2(cpuid)
    local cpu = c.me_interface.getCpus()[cpuid]['cpu']
    return cpu.storedItems()
end

function aeCpuDetail3(cpuid)
    local cpu = c.me_interface.getCpus()[cpuid]['cpu']
    return cpu.pendingItems()
end

function aeCpuDetail4(cpuid)
    local cpu = c.me_interface.getCpus()[cpuid]['cpu']
    return cpu.finalOutput()
end

function aeCancel(cpuid)
    local cpu = c.me_interface.getCpus()[cpuid]['cpu']
    cpu.cancel()
    return "DONE"
end

function aeCraftable()
    return c.me_interface.getItemsInNetwork({isCraftable=true})
end

function aeCraft(name, meta, amount)
    local u = c.me_interface.getCraftables({name=name, damage=meta})[1].request(amount, true)
    local b = ""
    if not u.isCanceled() then
        b = "DONE"
    else
        b = "ERROR"
    end
    print("Recipe task target -> "..name..":"..meta)
    print("Recipe task result -> "..b)
    return b
end